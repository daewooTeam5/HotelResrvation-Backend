package daewoo.team5.hotelreservation.domain.payment.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity,Long> {
    PaymentHistoryEntity findByPaymentId(Long paymentId);
}
