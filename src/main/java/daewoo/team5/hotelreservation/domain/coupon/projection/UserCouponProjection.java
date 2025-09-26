package daewoo.team5.hotelreservation.domain.coupon.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserCouponProjection {
    Long getUserId();
    Boolean getIsUsed();
    String getCouponName();
    LocalDateTime getExpiredAt();
    Integer getAmount();
    String getCouponType();
    Integer getMinOrderAmount();
    String getPlaceName();
    String getPlaceImageUrl();
    Long getPlaceId();

}
