package daewoo.team5.hotelreservation.global.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.global.exception.ErrorDetails;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorDetails errorDetails = new ErrorDetails(
                null,
                "Forbidden",
                403,
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );
        ApiResult<?> body = ApiResult.builder()
                .success(false)
                .message("권한 없음")
                .timestamp(LocalDateTime.now())
                .error(errorDetails)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(body));

    }
}
