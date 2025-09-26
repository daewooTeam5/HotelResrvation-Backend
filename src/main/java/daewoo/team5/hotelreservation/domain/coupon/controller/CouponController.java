package daewoo.team5.hotelreservation.domain.coupon.controller;

import daewoo.team5.hotelreservation.domain.coupon.dto.IssuedCouponDto;
import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.service.CouponService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/coupon")
public class CouponController {
    private final CouponService couponService;

    @AuthUser
    @PostMapping("/issue")
    public ApiResult<UserCouponEntity> getIssueCoupon(
            @RequestBody
            IssuedCouponDto dto,
            UserProjection user
    ) {
        return ApiResult.created(
                couponService.issueCoupon(dto.getCouponCode(), user),
                "쿠폰이 발급되었습니다."
        );
    }
}
