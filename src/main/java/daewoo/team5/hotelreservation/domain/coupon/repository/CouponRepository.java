package daewoo.team5.hotelreservation.domain.coupon.repository;


import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCouponCode(String couponCode);
}
