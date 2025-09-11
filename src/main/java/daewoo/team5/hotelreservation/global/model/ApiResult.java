package daewoo.team5.hotelreservation.global.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import daewoo.team5.hotelreservation.global.exception.ErrorDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> {
    private T data;
    private String message;
    private Boolean success;
    private ErrorDetails error;
    private LocalDateTime timestamp;

    @JsonIgnore
    private int status;

    public ApiResult<T> status(int status) {
        this.status = status;
        return this;
    }

    public ApiResult<T> message(String message) {
        this.message = message;
        return this;
    }

    public ApiResult<T> data(T data) {
        this.data = data;
        this.timestamp = LocalDateTime.now();
        return this;
    }

    public ApiResult<T> success(boolean success) {
        this.success = success;
        return this;
    }

    public ApiResult<T> error(ErrorDetails error) {
        this.error = error;
        return this;
    }

    private ApiResult<T> timestampNow() {
        this.timestamp = LocalDateTime.now();
        return this;
    }

    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<T>()
                .status(200)
                .data(data)
                .message("성공")
                .success(true)
                .timestampNow();
    }

    public static <T> ApiResult<T> ok(T data, String message) {
        return new ApiResult<T>()
                .status(200)
                .data(data)
                .message(message)
                .success(true)
                .timestampNow();
    }

    public static <T> ApiResult<T> created(T data) {
        return new ApiResult<T>()
                .status(201)
                .data(data)
                .message("생성됨")
                .success(true)
                .timestampNow();
    }

    public static <T> ApiResult<T> created(T data, String message) {
        return new ApiResult<T>()
                .status(201)
                .data(data)
                .message(message)
                .success(true)
                .timestampNow();
    }

}
