package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomRevenueDTO {
    private String roomType;   // 객실 타입 (예: "스탠다드", "스위트")
    private long reservationCount; // 예약 건수
    private long totalRevenue;     // 매출 합계
}