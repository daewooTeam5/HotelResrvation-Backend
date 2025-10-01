package daewoo.team5.hotelreservation.domain.discount.repository;


import daewoo.team5.hotelreservation.domain.discount.entity.DiscountHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountHistoryRepository extends JpaRepository<DiscountHistoryEntity,Long> {
}
