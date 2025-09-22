package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.place.dto.BedDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDTO {
    private int roomNumber;

    private int price;

    private int CapacityPeople;

    private int extraPrice;

    private String roomType;

    private String checkIn;

    private String checkOut;

    private List<BedDTO> bedType;

}