package daewoo.team5.hotelreservation.domain.coupon.service;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.projection.CouponIssuedProjection;
import daewoo.team5.hotelreservation.domain.coupon.projection.UserCouponProjection;
import daewoo.team5.hotelreservation.domain.coupon.repository.CouponRepository;
import daewoo.team5.hotelreservation.domain.coupon.repository.UserCouponRepository;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
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
    private final PlaceRepository placeRepository;

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
    public Integer calculateDiscountAmount(CouponEntity couponEntity, Integer orderAmount) {
        if (couponEntity.getCouponType() == CouponEntity.CouponType.fixed) {
            return Math.min(couponEntity.getAmount(), orderAmount);
        } else if (couponEntity.getCouponType() == CouponEntity.CouponType.rate) {
            int discount = (int) (orderAmount * (couponEntity.getAmount() / 100.0));
            // maxOrderAmount 가 -1이면 제한 없음
            if (couponEntity.getMaxOrderAmount() != -1) {
                return Math.min(discount, couponEntity.getMaxOrderAmount());
            } else {
                return discount;
            }
        }
        return 0;
    }
    // 해당 숙박업소에 사용 가능한 쿠폰인지 확인
    public Boolean validateCouponWithPlace(Long userId,CouponEntity couponEntity,Places places,Integer orderAmount) {
        UserCouponEntity userCouponEntity = userCouponRepository.findByUserIdAndCouponId(userId, couponEntity.getId()).orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "해당 유저가 발급받지 않은 쿠폰입니다.", "userId와 couponId를 확인해주세요."));
        // 숙박업소와 쿠폰의 숙박업소가 같은지 확인
        if(!couponEntity.getPlace().getId().equals(places.getId())){
            throw new ApiException(HttpStatus.BAD_REQUEST, "해당 숙박업소에 사용 불가능한 쿠폰입니다.", "couponId와 placeId를 확인해주세요.");
        }
        if(!couponEntity.isUsable(orderAmount)){
            log.info(couponEntity.getCouponType().name());
            throw new ApiException(HttpStatus.BAD_REQUEST, "해당 쿠폰은 사용 불가능한 쿠폰입니다.", "최소 주문금액이 주문금액보다 높습니다.");
        }
        // 쿠폰이 사용되었는지 확인
        if(userCouponEntity.isUsed()){
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 사용된 쿠폰입니다.", "couponId를 확인해주세요.");
        }
        return true;
    }

    public List<UserCouponProjection> getUserCoupons(Long userId, String type) {
        /**
         * 1. all : 전체 쿠폰
         * 2. used + expired  : 사용한 쿠폰 + 기간 만료된 쿠폰
         * 3. unused : 사용하지 않은 쿠폰 = 사용 가능
         */
        if (type.equals("all")) {
            // userId로 발급된 쿠폰 조회후 couponEntity 리스트로 반환
            return userCouponRepository.findAllByUserIdAndCouponAll(userId);
        }else if(type.equals("unused")){
            return userCouponRepository.findAllByUserIdAndCouponUsableCoupon(userId);
        }else if(type.equals("used_expired")){
            return userCouponRepository.findAllByUserIdAndCouponUsedAndExpiredCoupon(userId);
        }
        return userCouponRepository.findAllByUserIdAndCouponAll(userId);
    }


    public List<CouponEntity> getAvailableCoupon(UserProjection user,Long placeId) {
        return userCouponRepository.findUsableCouponsByUserIdAndPlaceId(user.getId(),placeId);
    }

    public List<CouponIssuedProjection> getUserCoupons(Long userId) {
        return userCouponRepository.findCouponsByUserId(userId);
    }
}
