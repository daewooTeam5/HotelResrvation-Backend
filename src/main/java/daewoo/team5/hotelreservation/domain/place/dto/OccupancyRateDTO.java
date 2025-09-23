package daewoo.team5.hotelreservation.domain.place.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OccupancyRateDTO {
    private int usedRooms;   // 사용 중 객실 수
    private int totalRooms;  // 전체 객실 수
    private double rate;     // 점유율 %
}