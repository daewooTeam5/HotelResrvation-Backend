package daewoo.team5.hotelreservation.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlyCountDto {
    private String month; // "2025-09"
    private long count;
}