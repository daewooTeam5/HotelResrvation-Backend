package daewoo.team5.hotelreservation.domain.coupon.repository;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponHistoryRepository extends JpaRepository<CouponHistoryEntity,Long> {
}
