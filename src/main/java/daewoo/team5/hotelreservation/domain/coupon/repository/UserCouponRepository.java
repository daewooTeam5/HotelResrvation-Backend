package daewoo.team5.hotelreservation.domain.coupon.repository;

import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity,Long> {
    boolean existsByUserIdAndCoupon_CouponCode(Long userId, String couponCode);
    List<UserCouponEntity> findByUserId(Long userId);
}
