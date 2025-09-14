package daewoo.team5.hotelreservation.domain.hotel.publishing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {    //주소 추가 입력

    private String province;    //주,도

    private String city;    //시,군,구

    private String town;    //동,읍,면

    private String road;    //도로명

    private String roadNumber;  //도로명 숫자

    private String postNumber;  //우편번호

    private String detailPost;    //상세주소

}