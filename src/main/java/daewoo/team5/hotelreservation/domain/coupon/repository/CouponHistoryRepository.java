package daewoo.team5.hotelreservation.domain.coupon.repository;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CouponHistoryRepository extends JpaRepository<CouponHistoryEntity, Long> {
    @Query(""" 
        select ch
        from CouponHistory ch
        join fetch ch.userCoupon c
        where ch.reservation_id.reservationId = :reservationId
    """)
    Optional<CouponHistoryEntity> findByReservation_id(Long reservationId);
}
