package daewoo.team5.hotelreservation.global.core.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.global.exception.ErrorDetails;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        ErrorDetails errorDetails = new ErrorDetails(
                null,
                "Unauthorized",
                401,
                authException.getMessage(),
                request.getRequestURI()
        );
        ErrorDetails error = new ErrorDetails(
                null,
                "Unauthorized",
                401,
                authException.getMessage(),
                request.getRequestURI()
        );
        ApiResult<?> unauthorized =
                new ApiResult<>()
                        .status(401)
                        .message("Unauthorized")
                        .error(error)
                        .success(false);

        response.getWriter().write(objectMapper.writeValueAsString(unauthorized));


    }
}
