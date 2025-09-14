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
    // error 에 관련된 문서 링크
    private String type;
    // error 제목
    private String title;
    // HTTP 상태 코드
    private Integer status;
    // error 상세 내용
    private String detail;
    // error 가 발생한 instance URI
    private String instance;
    // custom error code
    private String errorCode;

    public ErrorDetails(String type, String title, Integer status, String detail, String instance) {
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

}
