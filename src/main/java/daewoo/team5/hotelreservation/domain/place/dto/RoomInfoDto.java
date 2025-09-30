package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomInfoDto {
    private Long roomId;
    private String roomType;
    private String bedType;
    private Integer capacityPeople;
    private Integer capacityRoom;
    private Double price;
    private String status;
    private Integer availableRoom;
    private Double area;
    private List<String> images;
    private Integer isAvailable;
    private List<AmenityDto> amenities; // ✅ name + icon 담을 DTO 리스트
}
