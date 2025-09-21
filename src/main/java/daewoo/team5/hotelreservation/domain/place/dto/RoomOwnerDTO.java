package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOwnerDTO {
    private Long id;
    private Long placeId;
    private String roomType;
    private String bedType;
    private Integer capacityPeople;
    private Integer capacityRoom;
    private BigDecimal price;
    private Room.Status status;
}
