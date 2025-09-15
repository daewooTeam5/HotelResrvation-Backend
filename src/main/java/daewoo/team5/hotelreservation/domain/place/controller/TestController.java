package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.controller.swagger.TestSwagger;
import daewoo.team5.hotelreservation.domain.place.dto.TestDto;
import daewoo.team5.hotelreservation.domain.place.entity.MyTest;
import daewoo.team5.hotelreservation.global.provider.JwtProvider;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 삭제 예정
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController implements TestSwagger {
    private final JwtProvider jwtProvider;

    @Override
    @PostMapping("/test")
    public ApiResult<Map<String,String>> testPost(@Valid @RequestBody TestDto dto, HttpServletRequest request) {
        if (dto.getName().equals("mangosaet")){
            throw new ApiException(403, "Forbidden", "접근 금지된 유저 입니다.");
        }
        String accessToken = jwtProvider.generateToken(dto, JwtProvider.TokenType.ACCESS);
        String refreshToken = jwtProvider.generateToken(dto, JwtProvider.TokenType.REFRESH);
        return new ApiResult<Map<String,String>>()
                .status(201)
                .message("Created")
                .success(true)
                .data(
                        Map.of(
                                "accessToken", accessToken,
                                "refreshToken", refreshToken
                        )
                );
    }

    @Override
    @GetMapping("/test")
    public ApiResult<MyTest> test() {
        return new ApiResult<MyTest>()
                .status(200)
                .message("No Content")
                .success(true)
                .data(
                        MyTest.builder()
                                .name("ad")
                                .build()
                );
    }

}
