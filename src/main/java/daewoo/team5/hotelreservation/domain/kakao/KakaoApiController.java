package daewoo.team5.hotelreservation.domain.kakao;

import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * 카카오 API와 관련된 키 값을 제공하는 컨트롤러입니다.
 */
@RestController
// ⭐️ API의 기본 경로를 /api/v1/kakao로 설정합니다.
@RequestMapping("/api/v1/kakao")
public class KakaoApiController {

    /**
     * .env.properties 또는 application.yml 파일에 설정된 카카오 API 키 값을 주입받습니다.
     */
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    /**
     * 프론트엔드에 카카오 지도 API 키를 제공하는 엔드포인트입니다.
     * @return {"apiKey": "실제 API 키"} 형태의 JSON 객체
     */
    // ⭐️ 최종 경로는 /api/v1/kakao/map-key가 됩니다.
    @GetMapping("/map-key")
    public ApiResult<Map<String, String>> getKakaoMapKey() {
        // 키 이름과 값을 Map에 담아 ApiResult.ok()로 감싸서 성공 응답을 반환합니다.
        Map<String, String> response = Collections.singletonMap("apiKey", kakaoApiKey);
        return ApiResult.ok(response);
    }
}