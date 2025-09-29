package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChartDataResponse {
    private String period;  // ex) "2025-09-28", "2025-09", "2025"
    private Number value;
}