package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayNewGuestDTO {
    private long todayNewGuests;
    private double growthRate; // 전일 대비 %
}