package daewoo.team5.hotelreservation.domain.discount.repository;

import daewoo.team5.hotelreservation.domain.discount.entity.ReservationDiscountHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationDiscountHistoryRepository extends JpaRepository<ReservationDiscountHistoryEntity, Long> {
}
