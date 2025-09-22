package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BedDTO {

    private String type;   // 침대 종류

    private String width;  // 침대 폭

    private int count;     // 개수

}
