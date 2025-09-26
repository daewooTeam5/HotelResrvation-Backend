package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopHotelDto {
    private String hotelName;
    private long value; // 매출액 or 예약 수
}