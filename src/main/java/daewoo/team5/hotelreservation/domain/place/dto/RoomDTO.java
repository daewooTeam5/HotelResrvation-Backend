package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {

    private int roomNumber;

    private int price;

    private int maxCount;


}
