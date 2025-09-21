package daewoo.team5.hotelreservation.global.exception;


import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException{
    public UserNotFoundException() {
        super(
                HttpStatus.NOT_FOUND,
                "Users Not Found",
                "유저 정보가 존재 하지 않습니다.",
                "E404U001"
        );
    }

}
