package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {    //주소 추가 입력

    private String sigungu;

    private String sido;

    private String town;

    private String roadName;

    private String postalCode;

    private String detailAddress;

}