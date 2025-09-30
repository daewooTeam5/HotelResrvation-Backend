package daewoo.team5.hotelreservation.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // 요청 DTO이므로 Setter 유지
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateDto {
    private String couponName;
    private String couponType;
    private Integer amount;
    private Integer minOrderAmount;
    private Integer maxOrderAmount;
    private String expiredAt;
}