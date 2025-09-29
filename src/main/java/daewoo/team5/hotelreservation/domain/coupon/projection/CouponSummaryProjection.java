package daewoo.team5.hotelreservation.domain.coupon.projection;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import java.time.LocalDateTime;

public interface CouponSummaryProjection {
    Long getId();
    String getCouponName();
    CouponEntity.CouponType getCouponType();
    Integer getAmount();
    LocalDateTime getCreatedAt();
    LocalDateTime getExpiredAt();
    String getCouponCode();
    Integer getMinOrderAmount();
    Integer getMaxOrderAmount();
    Long getPlaceId();
}

