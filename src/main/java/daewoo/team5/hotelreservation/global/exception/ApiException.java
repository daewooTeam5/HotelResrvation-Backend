package daewoo.team5.hotelreservation.global.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApiException extends RuntimeException {
    private final ErrorDetails error;

    public ApiException(ErrorDetails error) {
        super(error.getDetail());
        this.error = error;
    }

    public ApiException(HttpStatus status, String title, String detail) {
        this.error = new ErrorDetails(null, title, status.value(), detail, null);
    }

    public ApiException(HttpStatus status, String title, String detail, String errorCode) {
        this.error = new ErrorDetails(null, title, status.value(), detail, null, errorCode);
    }

    public ApiException(int status, String title, String detail) {
        this.error = new ErrorDetails(null, title, status, detail, null);
    }

    public ApiException(int status, String title, String detail, String errorCode) {
        this.error = new ErrorDetails(null, title, status, detail, null, errorCode);
    }

    public ApiException(int statusCode, String title, String detail, HttpServletRequest request) {
        this.error = new ErrorDetails(null, title, statusCode, detail, request.getRequestURI());
    }
}
