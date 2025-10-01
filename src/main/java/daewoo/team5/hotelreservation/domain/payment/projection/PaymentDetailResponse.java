package daewoo.team5.hotelreservation.domain.payment.projection;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface PaymentDetailResponse {
    Long getPaymentId();
    String getPaymentKey();
    Long getAmount();
    LocalDateTime getTransactionDate();
    String getMethod();
    String getStatus();
    String getMethodType();
    BigDecimal getBaseAmount();
    BigDecimal getFinalAmount();
    Integer getFixedDiscountAmount();
    Integer getCouponDiscountAmount();
    Integer getPointDiscountAmount();
}