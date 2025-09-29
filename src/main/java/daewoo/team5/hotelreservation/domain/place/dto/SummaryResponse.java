package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummaryResponse {
    private long reservationCount;
    private long revenue;
    private double cancelRate;
}