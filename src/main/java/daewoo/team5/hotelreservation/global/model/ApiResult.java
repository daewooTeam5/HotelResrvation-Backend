package daewoo.team5.hotelreservation.global.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResult<T> {

    private boolean success; // 성공 여부
    private T data;          // 실제 데이터
    private String message;  // 부가 메시지

    // 성공 응답 (조회용)
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(true, data, null);
    }

    // 생성 응답 (등록/생성용)
    public static <T> ApiResult<T> created(T data, String message) {
        return new ApiResult<>(true, data, message);
    }

    // 실패 응답
    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(false, null, message);
    }
}