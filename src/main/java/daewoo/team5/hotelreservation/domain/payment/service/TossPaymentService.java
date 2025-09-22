package daewoo.team5.hotelreservation.domain.payment.service;

import daewoo.team5.hotelreservation.domain.payment.dto.TossCancelResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
public class TossPaymentService {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private final RestTemplate restTemplate = new RestTemplate();

    public TossCancelResponse cancelPayment(String paymentKey, String cancelReason) {
        // ✅ Basic Auth 헤더 생성
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8));

        // ✅ 요청 헤더 세팅
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        headers.set("Idempotency-Key", UUID.randomUUID().toString());

        // ✅ 요청 Body
        Map<String, String> body = Map.of("cancelReason", cancelReason);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        // ✅ API 호출
        ResponseEntity<TossCancelResponse> response = restTemplate.exchange(
                "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel",
                HttpMethod.POST,
                entity,
                TossCancelResponse.class
        );

        return response.getBody();
    }
}
