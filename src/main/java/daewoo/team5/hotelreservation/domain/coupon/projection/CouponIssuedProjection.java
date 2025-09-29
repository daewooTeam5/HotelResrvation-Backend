package daewoo.team5.hotelreservation.domain.coupon.projection;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;

import java.time.LocalDateTime;

public interface CouponIssuedProjection {
    Long getCouponId();
    String getCouponName();
    String getCouponCode();
    Integer getAmount();
    CouponEntity.CouponType getCouponType();
    boolean getIsUsed();
    LocalDateTime getIssuedAt();
    LocalDateTime getExpiredAt();
}
