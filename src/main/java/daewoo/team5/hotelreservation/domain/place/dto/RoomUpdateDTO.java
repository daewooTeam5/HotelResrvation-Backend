package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RoomUpdateDTO {
    private int roomNumber;

    private String roomType;

    private int capacityPeople;

    private int minPrice;

    private int extraPrice;

    private String bedType;

//    private boolean isPublic;

    private Integer capacityRoom;

    private List<Long> amenityIds;

}