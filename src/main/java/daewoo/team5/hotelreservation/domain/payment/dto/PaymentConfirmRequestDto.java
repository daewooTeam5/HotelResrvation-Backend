package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequestDto {
    private String paymentKey;
    private String orderId;
    private Long amount;
}
