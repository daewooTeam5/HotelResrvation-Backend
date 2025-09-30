package daewoo.team5.hotelreservation.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDetailDto {
    private Long id;
    private String couponName;
    private String couponCode;
    private String couponType;
    private Integer amount;
    private Integer minOrderAmount;
    private Integer maxOrderAmount;
    private String createdAt;
    private String expiredAt;
    private List<CouponHistoryDto> history;
    private Long usedCount;
}