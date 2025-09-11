package daewoo.team5.hotelreservation.global.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// RFC 9457 Problem Details for HTTP APIs
// 참고 주소 : https://www.rfc-editor.org/rfc/rfc9457.html
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;
}
