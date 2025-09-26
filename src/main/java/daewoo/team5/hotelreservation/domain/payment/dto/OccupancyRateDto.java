package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OccupancyRateDto {
    private String placeName;
    private double occupancyRate; // 점유율 (%)
}