package daewoo.team5.hotelreservation.domain.coupon.controller;

import daewoo.team5.hotelreservation.domain.coupon.dto.CouponCreateDto;
import daewoo.team5.hotelreservation.domain.coupon.dto.CouponDetailDto;
import daewoo.team5.hotelreservation.domain.coupon.dto.CouponListDto;
import daewoo.team5.hotelreservation.domain.coupon.service.CouponOwnerService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/coupons")
@RequiredArgsConstructor
public class CouponOwnerController {

    private final CouponOwnerService couponOwnerService;

    /**  쿠폰 목록 조회 */
    @GetMapping
    @AuthUser
    public Page<CouponListDto> getCoupons(UserProjection projection, Pageable pageable) {
        return couponOwnerService.getCoupons(projection.getId(), pageable);
    }

    /**  쿠폰 상세 조회 */
    @GetMapping("/{couponId}")
    public CouponDetailDto getCouponDetail(
            @PathVariable Long couponId,
            Pageable pageable
    ) {
        return couponOwnerService.getCouponDetail(couponId, pageable);
    }

    /**  쿠폰 생성 */
    @PostMapping
    @AuthUser
    public Long createCoupon(UserProjection projection, @RequestBody CouponCreateDto dto) {
        return couponOwnerService.createCoupon(projection.getId(), dto);
    }
}
