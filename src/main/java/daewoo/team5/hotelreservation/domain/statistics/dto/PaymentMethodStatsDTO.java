package daewoo.team5.hotelreservation.domain.statistics.dto;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentMethodStatsDTO {
    private Payment.PaymentMethod method; // enum 타입 그대로 반환
    private long count;
    private long totalAmount;
}