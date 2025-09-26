package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StayDurationDistributionDTO {
    private String label;  // ex) "1일", "2일", ...
    private long count;    // 고객 수
}