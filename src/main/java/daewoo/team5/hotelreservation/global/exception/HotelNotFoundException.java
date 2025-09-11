package daewoo.team5.hotelreservation.global.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public class HotelNotFoundException extends ApiException {
    public HotelNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                null,
                "Hotel Not Found",
                "호텔 정보가 존재 하지 않습니다.",
                null
        );
    }
}
