package daewoo.team5.hotelreservation.domain.coupon.repository;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CouponHistoryRepository extends JpaRepository<CouponHistoryEntity, Long> {
    @Query(""" 
        select ch
        from CouponHistory ch
        join fetch ch.userCoupon c
        where ch.reservation.reservationId = :reservationId and ch.status='pending'
    """)
    Optional<CouponHistoryEntity> findByReservation_idWithPending(Long reservationId);
    @Query(""" 
        select ch
        from CouponHistory ch
        join fetch ch.userCoupon c
        where ch.reservation.reservationId = :reservationId and ch.status='used'
    """)
    Optional<CouponHistoryEntity> findByReservation_idWithUsed(Long reservationId);

    Page<CouponHistoryEntity> findByUserCoupon_Coupon_Id(Long couponId, Pageable pageable);

    Page<CouponHistoryEntity> findAllByUserCoupon_Coupon_Id(Long couponId, Pageable pageable);
}
