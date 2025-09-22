package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {    //주소 추가 입력

    private String sigungu;    //시,군,구

    private String sido;    //시,도(특별시)

    private String roadName;    //도로명

    private String postalNumber;  //우편번호

    private String detailAddress;    //상세주소

}