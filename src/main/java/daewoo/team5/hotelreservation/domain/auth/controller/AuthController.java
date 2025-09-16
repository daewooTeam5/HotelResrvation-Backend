package daewoo.team5.hotelreservation.domain.auth.controller;

import daewoo.team5.hotelreservation.domain.auth.controller.swagger.AuthSwagger;
import daewoo.team5.hotelreservation.domain.auth.dto.AuthCodeDto;
import daewoo.team5.hotelreservation.domain.auth.dto.EmailLoginDto;
import daewoo.team5.hotelreservation.domain.auth.dto.LoginSuccessDto;
import daewoo.team5.hotelreservation.domain.auth.service.AuthService;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.core.provider.CookieProvider;
import daewoo.team5.hotelreservation.global.core.provider.JwtProvider;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthSwagger {
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @PostMapping("/auth")
    public ApiResult<Boolean> emailLogin(@RequestBody @Valid EmailLoginDto emailLoginDto) {
        log.info("Email Login Request Received: {}", emailLoginDto);
        authService.sendOtpCode(emailLoginDto.getEmail());
        return ApiResult.ok(true, "인증 코드가 이메일로 전송되었습니다.");
    }

    @PostMapping("/logout")
    public ApiResult<Boolean> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ){
        // refreshToken 쿠키 삭제
        cookieProvider.removeCookie("refreshToken",response);

        authService.logout(refreshToken);
        return ApiResult.ok(true, "로그아웃 되었습니다.");


    }

    @PostMapping("/auth/code")
    public ApiResult<LoginSuccessDto> authOtpCode(
            @RequestBody
            @Valid
            AuthCodeDto authCodeDto,
            HttpServletResponse response
    ) {
        UserProjection users = authService.authLogInOtpCode(authCodeDto.getEmail(), authCodeDto.getCode());
        String accessToken = jwtProvider.generateToken(users, JwtProvider.TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(users.getId(), JwtProvider.TokenType.REFRESH);
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60);
        response.addCookie(cookie);
        LoginSuccessDto loginSuccessDto = new LoginSuccessDto(accessToken, users);
        return ApiResult.ok(loginSuccessDto, "인증 성공");
    }

    @PostMapping("auth/token")
    public ApiResult<Map<String,String>> reissueToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED.value(), "토큰 재발급 실패", "리프레시 토큰이 없습니다. 다시 로그인 해주세요.");
        }
        String accessToken = authService.reissueToken(refreshToken,response);
        return ApiResult.ok(Map.of("accessToken", accessToken), "토큰 재발급 성공");
    }


}
