package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DiscountDTO {

    private int person;      // 인원

    private int discount;    // % 할인
}