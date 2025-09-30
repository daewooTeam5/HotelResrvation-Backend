package daewoo.team5.hotelreservation.domain.coupon.repository;


import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.projection.CouponSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    Optional<CouponEntity> findByCouponCode(String couponCode);

    @Query("""
        select 
            c.id               as id,
            c.couponName       as couponName,
            c.couponType       as couponType,
            c.amount           as amount,
            c.createdAt        as createdAt,
            c.expiredAt        as expiredAt,
            c.couponCode       as couponCode,
            c.minOrderAmount   as minOrderAmount,
            c.maxOrderAmount   as maxOrderAmount,
            c.place.id         as placeId
        from Coupon c
        where c.id = :couponId
    """)
    Optional<CouponSummaryProjection> findCouponSummaryById(@Param("couponId") Long couponId);

    // placeId 기준 페이징 조회
    Page<CouponEntity> findAllByPlace_Id(Long placeId, Pageable pageable);

    @Query("""
        select count(ch)
        from CouponHistory ch
        where ch.userCoupon.coupon.id = :couponId and ch.status = 'used'
    """)
    long countUsedByCouponId(@Param("couponId") Long couponId);
}
