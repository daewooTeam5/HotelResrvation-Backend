package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TodayReturnGuestDTO {
    private long todayReturnGuests;
    private double growthRate;
}