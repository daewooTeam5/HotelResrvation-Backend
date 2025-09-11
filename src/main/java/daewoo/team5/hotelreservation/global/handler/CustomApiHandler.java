package daewoo.team5.hotelreservation.global.handler;


import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.ErrorDetails;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class CustomApiHandler implements ResponseBodyAdvice<Object> {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleException(Exception e, HttpServletRequest request) {
        log.error("Exception: ", e);
        ErrorDetails errorDetails = new ErrorDetails(
                null,
                "서버 오류",
                500,
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(500).body(
                ApiResult.builder()
                        .message("서버 오류")
                        .success(false)
                        .timestamp(LocalDateTime.now())
                        .error(errorDetails)
                        .build()
        );
    }

    @ExceptionHandler(ApiException.class)
    public ApiResult<?> handleApiException(ApiException e, HttpServletRequest request, HttpServletResponse response) {
        ErrorDetails error = e.getError();
        if (error.getInstance() == null) {
            error.setInstance(request.getRequestURI());
        }
        response.setStatus(error.getStatus());

        return
                ApiResult.builder()
                        .message(error.getTitle())
                        .success(false)
                        .timestamp(LocalDateTime.now())
                        .error(error)
                        .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<?>> handleValidationException(MethodArgumentNotValidException ex,
                                                                  HttpServletRequest request) {
        log.error("Validation Error: ", ex);

        // 필드별 에러 메시지 맵핑
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        ErrorDetails errorDetails = new ErrorDetails(
                null,
                "검증 오류",
                400,
                validationErrors.toString(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(
                ApiResult.builder()
                        .message("입력값 검증 실패")
                        .success(false)
                        .timestamp(LocalDateTime.now())
                        .error(errorDetails)
                        .build()
        );
    }

    // 특정 타입에서만 beforeBodyWrite 가 작동하도록 설정
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getParameterType().equals(ApiResult.class);
    }

    // 응답전에 바디 가공 APIResult 타입의 응답코드를 실제 response 에 설정
    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        // 응답 타입이 ApiResult 인지 검증
        if (body instanceof ApiResult) {
            ApiResult<?> apiResult = (ApiResult<?>) body;
            // api 요청 성공시에만 상태코드 설정 (실패는 ExceptionHandler 에서 처리)
            if (apiResult.getSuccess()) {
                response.setStatusCode(HttpStatus.valueOf(apiResult.getStatus()));
            }
            return body;
        }
        return body;
    }
}
