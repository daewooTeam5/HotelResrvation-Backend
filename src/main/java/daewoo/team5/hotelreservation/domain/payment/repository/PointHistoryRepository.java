package daewoo.team5.hotelreservation.domain.payment.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity,Long> {
}
