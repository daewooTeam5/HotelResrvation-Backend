package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationTrendDTO {
    private String label;   // x축 라벨 (날짜, 주, 월, 년)
    private long count;     // 예약 건수
}