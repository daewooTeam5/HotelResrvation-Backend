package daewoo.team5.hotelreservation.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopCustomerDto {
    private Long userId;
    private long value; // 예약건수 or 결제금액
}