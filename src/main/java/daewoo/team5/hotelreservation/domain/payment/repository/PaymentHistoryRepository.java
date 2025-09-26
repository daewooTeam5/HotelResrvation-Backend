package daewoo.team5.hotelreservation.domain.payment.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.PaymentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistoryEntity,Long> {

}
