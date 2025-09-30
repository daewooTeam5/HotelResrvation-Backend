package daewoo.team5.hotelreservation.domain.coupon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponHistoryDto {
    private Long id;
    private String userName;
    private Long userCouponId;
    private Long reservationId;
    private Integer discountAmount;
    private String usedAt;
    private String status;
}