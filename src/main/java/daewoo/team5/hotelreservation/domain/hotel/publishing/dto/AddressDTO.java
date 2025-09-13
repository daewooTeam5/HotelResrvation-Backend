package daewoo.team5.hotelreservation.domain.hotel.publishing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {    //주소 추가 입력

    private String province;

    private String city;

    private String town;

    private String road;

    private String roadNumber;

    private String postNumber;

    private String detailPost;

}