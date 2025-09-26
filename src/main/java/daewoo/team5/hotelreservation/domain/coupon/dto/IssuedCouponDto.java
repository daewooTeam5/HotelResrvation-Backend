package daewoo.team5.hotelreservation.domain.coupon.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IssuedCouponDto {
    private String couponCode;
}
