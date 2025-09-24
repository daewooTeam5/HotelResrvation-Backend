package daewoo.team5.hotelreservation.domain.coupon.service;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final UsersRepository usersRepository;

    public UserCouponEntity issueCoupon(String couponCode, UserProjection user) {
        Users couponIssuer = usersRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        CouponEntity couponEntity = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(
                        () -> new ApiException(HttpStatus.BAD_REQUEST, "존재하지 않는 쿠폰 코드입니다.", "쿠폰 코드를 확인해주세요.")
                );
        // 중복 발급 방지
        if (userCouponRepository.existsByUserIdAndCoupon_CouponCode(user.getId(), couponCode)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 발급된 쿠폰입니다.", "쿠폰 코드를 확인해주세요.");
        }
        // 발급 가능한 쿠폰인지 확인
        if (!couponEntity.isIssuable()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "유효기간이 지난 쿠폰입니다.", "쿠폰 코드를 확인해주세요.");
        }
        return userCouponRepository.save(
                UserCouponEntity.builder()
                        .isUsed(false)
                        .issuedAt(LocalDateTime.now())
                        .coupon(couponEntity)
                        .user(couponIssuer)
                        .build()
        );


    }

    public List<CouponEntity> getUserCoupons(Long userId) {
        return null;


    }
}
