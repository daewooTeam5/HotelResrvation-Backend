package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 예약 현황 DTO
 * - 오늘 현황용 (todayReservations, todayCheckIn, todayCheckOut, growthRate)
 * - 월별 현황용 (month, count)
 */
@Getter
@Setter
@NoArgsConstructor
public class ReservationStatsDTO {

    // ===== 오늘 현황 =====
    private long todayReservations;
    private long todayCheckIn;
    private long todayCheckOut;
    private double growthRate;

    // ===== 월별 현황 =====
    private String month;  // YYYY-MM
    private Long count;

    // 오늘 현황 생성자
    public ReservationStatsDTO(long todayReservations, long todayCheckIn, long todayCheckOut, double growthRate) {
        this.todayReservations = todayReservations;
        this.todayCheckIn = todayCheckIn;
        this.todayCheckOut = todayCheckOut;
        this.growthRate = growthRate;
    }

    // 월별 현황 생성자
    public ReservationStatsDTO(String month, Long count) {
        this.month = month;
        this.count = count;
    }
}
