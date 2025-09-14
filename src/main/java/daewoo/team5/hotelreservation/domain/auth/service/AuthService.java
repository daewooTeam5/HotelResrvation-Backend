package daewoo.team5.hotelreservation.domain.auth.service;

import daewoo.team5.hotelreservation.domain.auth.repository.OtpRepository;
import daewoo.team5.hotelreservation.domain.users.entity.User;
import daewoo.team5.hotelreservation.domain.users.repository.UserRepository;
import daewoo.team5.hotelreservation.global.core.provider.JwtProvider;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.mail.service.MailService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    public void sendOtpCode(String email) {
        String optCode = otpRepository.generateOtp(email);
        log.info("Generated OTP Code: {}", optCode);
        mailService.sendOtpCode(email, optCode);
    }

    @Transactional
    public User authLogInOtpCode(String email, String code) {
        boolean isValid = otpRepository.validateOtp(email, code);
        if (!isValid) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 실패", "유효하지 않은 인증 코드입니다.");
        }
        Optional<User> findUser = userRepository.findByEmail(email);
        Random random = new Random();
        return findUser.orElseGet(() -> userRepository.save(
                User.builder()
                        .email(email)
                        .name("Guest" + random.nextInt())
                        .userId(UUID.randomUUID().toString())
                        .role(User.Role.customer)
                        .status(User.Status.active)
                        .build()

        ));

    }

    public String reissueToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || !jwtProvider.validateToken(refreshToken)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "토큰 재발급 실패", "유효하지 않은 리프레시 토큰입니다.");
        }
        Claims tokenParse = jwtProvider.parseClaims(refreshToken);
        tokenParse.getSubject();
        Long userId = Long.parseLong(tokenParse.getSubject());
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "사용자 없음", "해당 사용자가 존재하지 않습니다.")
        );
        String newAccessToken = jwtProvider.generateToken(user, JwtProvider.TokenType.ACCESS);
        String newRefreshToken = jwtProvider.generateToken(user.getId(), tokenParse.getExpiration().getTime());
        Cookie refreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge((int) ((tokenParse.getExpiration().getTime() - System.currentTimeMillis()) / 1000));
        response.addCookie(refreshTokenCookie);
        return newAccessToken;
    }
}
