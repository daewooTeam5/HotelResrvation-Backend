package daewoo.team5.hotelreservation.domain.payment.infrastructure;


import daewoo.team5.hotelreservation.domain.payment.dto.PaymentConfirmRequestDto;
import daewoo.team5.hotelreservation.domain.payment.dto.TossPaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "tossPayClient", url = "https://api.tosspayments.com")
public interface TossPayClient {
    //    @Value("${TOSS_SECRET_KEY}")
//    private String tossSecretKey;

    @PostMapping("/v1/payments/confirm")
    TossPaymentDto confirmPayment(@RequestBody PaymentConfirmRequestDto requestDto);

    @PostMapping("/v1/payments/cancel")
    TossPaymentDto cancelPayment(@RequestBody Map<String, String> requestDto);
}
