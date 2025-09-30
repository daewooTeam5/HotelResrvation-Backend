package daewoo.team5.hotelreservation.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.auth.dto.AdminLoginDto;
import daewoo.team5.hotelreservation.domain.auth.dto.LoginSuccessDto;
import daewoo.team5.hotelreservation.domain.auth.dto.SignUpRequest;
import daewoo.team5.hotelreservation.domain.auth.repository.BlackListRepository;
import daewoo.team5.hotelreservation.domain.auth.repository.OtpRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.core.provider.CookieProvider;
import daewoo.team5.hotelreservation.global.core.provider.JwtProvider;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.mail.service.MailService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final OtpRepository otpRepository;
    private final MailService mailService;
    private final UsersRepository userRepository;
    private final BlackListRepository blackListRepository;
    private final CookieProvider cookieProvider;
    private final JwtProvider jwtProvider;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // null 이면 비회원, null 아니면 회원
    public UserProjection isAuthUser(Authentication auth){
        UserProjection currentUser = null;
        if (auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            Object principal = auth.getPrincipal();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = null;
            try {
                node = mapper.readTree(principal.toString());
            } catch (JsonProcessingException e) {
                throw new ApiException(500, "서버 오류", "서버 오류가 발생했습니다. 개발자에게 문의해주세요");
            }
            currentUser = usersRepository.findById(Long.parseLong(node.toString()), UserProjection.class)
                    .orElseThrow(() -> new ApiException(404, "존재하지 않는 유저", "존재 하지 않는 유저입니다."));
        }
        return currentUser;
    }
    public void logout(String refreshToken) {
        long expirationTime = jwtProvider.parseClaims(refreshToken).getExpiration().getTime();
        blackListRepository.addToBlackList(refreshToken,expirationTime);
    }

    public Users adminSignUp(SignUpRequest signUpRequest) {
        signUpRequest.setAdminPassword(passwordEncoder.encode(signUpRequest.getAdminPassword()));

        Users.Role role;
        try {
            role = Users.Role.valueOf(signUpRequest.getAdminRole());
        } catch (IllegalArgumentException e) {
            role = Users.Role.admin;
        }

        return userRepository.save(Users.builder()
                .email(signUpRequest.getAdminId() + "@daewoo.ac.kr")
                .name(signUpRequest.getAdminName())
                .userId(signUpRequest.getAdminId())
                .password(signUpRequest.getAdminPassword())
                .role(role)
                .status(Users.Status.inactive)
                .build());
    }

    public LoginSuccessDto adminLogin(AdminLoginDto adminLoginDto, HttpServletResponse response){
        Users admin = userRepository.findByUserId(adminLoginDto.getAdminId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"사용자 없음","해당 관리자가 존재하지 않습니다."));
        if(!passwordEncoder.matches(adminLoginDto.getAdminPassword(),admin.getPassword())){
            throw new ApiException(HttpStatus.UNAUTHORIZED,"로그인 실패","비밀번호가 일치하지 않습니다.");
        }
        UserProjection projection = userRepository.findById(admin.getId(), UserProjection.class)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "사용자 없음", "해당 관리자가 존재하지 않습니다."));

        String accessToken = jwtProvider.generateToken(projection, JwtProvider.TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(projection.getId(), JwtProvider.TokenType.REFRESH);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30일
        response.addCookie(cookie);

        return new LoginSuccessDto(accessToken, projection);
    }

    public void sendOtpCode(String email) {
        String optCode = otpRepository.generateOtp(email);
        log.info("Generated OTP Code: {}", optCode);
        mailService.sendOtpCode(email, optCode);
    }

    @Transactional
    public UserProjection authLogInOtpCode(String email, String code) {
        boolean isValid = otpRepository.validateOtp(email, code);
        if (!isValid) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 실패", "유효하지 않은 인증 코드입니다.");
        }
        Optional<Users> findUser = userRepository.findByEmail(email);
        Random random = new Random();
        Users users = findUser.orElseGet(() -> userRepository.save(
                Users.builder()
                        .email(email)
                        .point(0L)
                        .name("Guest" + random.nextInt())
                        .userId(UUID.randomUUID().toString())
                        .role(Users.Role.customer)
                        .status(Users.Status.active)
                        .build()

        ));
        return userRepository.findByName(users.getName(),UserProjection.class).get();

    }

    public String reissueToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "토큰 재발급 실패", "유효하지 않은 리프레시 토큰입니다.");
        }
        if( blackListRepository.isBlackListed(refreshToken)) {
            cookieProvider.removeCookie("refreshToken",response);
            throw new ApiException(HttpStatus.UNAUTHORIZED, "토큰 재발급 실패", "예기치 못한 오류 발생");
        }
        Claims tokenParse = jwtProvider.parseClaims(refreshToken);
        tokenParse.getSubject();
        Long userId = Long.parseLong(tokenParse.getSubject());
        UserProjection users = userRepository.findById(userId,UserProjection.class).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "사용자 없음", "해당 사용자가 존재하지 않습니다.")
        );
        String newAccessToken = jwtProvider.generateToken(users, JwtProvider.TokenType.ACCESS);
        String newRefreshToken = jwtProvider.generateToken(users.getId(), tokenParse.getExpiration().getTime());
        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) ((tokenParse.getExpiration().getTime() - System.currentTimeMillis()) / 1000));
        response.addCookie(refreshTokenCookie);
        return newAccessToken;
    }
}
