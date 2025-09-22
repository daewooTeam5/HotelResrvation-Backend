package daewoo.team5.hotelreservation.domain.payment.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class TossCancelResponse {
    private String paymentKey;
    private String orderId;
    private String status;
    private List<CancelHistory> cancels;

    @Data
    public static class CancelHistory {
        private Long cancelAmount;
        private String cancelReason;
        private OffsetDateTime canceledAt;
        private String transactionKey;
    }
}