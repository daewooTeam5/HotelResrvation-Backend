package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.place.dto.BedDTO;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {

    private int roomNumber;

    private String roomType;

    private int capacityPeople;

    private int minPrice;

    private int extraPrice;

    private String bedType;

    private boolean isPublic;

    private Integer capacityRoom;


}