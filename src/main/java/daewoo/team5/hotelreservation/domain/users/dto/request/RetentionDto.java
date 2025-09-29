package daewoo.team5.hotelreservation.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RetentionDto {
    private String month;  // "2025-08"
    private double retentionRate;
}