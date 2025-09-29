package daewoo.team5.hotelreservation.domain.coupon.repository;

import daewoo.team5.hotelreservation.domain.coupon.entity.CouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.entity.UserCouponEntity;
import daewoo.team5.hotelreservation.domain.coupon.projection.CouponIssuedProjection;
import daewoo.team5.hotelreservation.domain.coupon.projection.UserCouponProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity,Long> {

    boolean existsByUserIdAndCoupon_CouponCode(Long userId, String couponCode);

    List<UserCouponEntity> findAllByUserId(Long userId);

    Optional<UserCouponEntity> findByUserIdAndCouponId(Long userId, Long couponId);

    Optional<UserCouponEntity> findByCouponId(Long couponId);

    @Query("""
            select uc.user.id as userId,uc.isUsed as isUsed,
                    uc.coupon.couponName as couponName,
                    uc.coupon.expiredAt as expiredAt,
                    uc.coupon.amount as amount,
                    uc.coupon.couponType as couponType,
                    uc.coupon.minOrderAmount as minOrderAmount,
                    p.name as placeName,
                    f.url as placeImageUrl,
                    p.id as placeId
             from UserCoupon uc
             join Places p on uc.coupon.place.id = p.id
             join File f on f.id = (
                 select min(f2.id)
                 from File f2
                 where f2.domainFileId = p.id
             )
             where uc.user.id = :userId
             and (uc.isUsed = true
             or uc.coupon.expiredAt < current_timestamp)
             """)
    List<UserCouponProjection> findAllByUserIdAndCouponUsedAndExpiredCoupon(Long userId);
    @Query("""
            select uc.user.id as userId,uc.isUsed as isUsed,
                    uc.coupon.couponName as couponName,
                    uc.coupon.expiredAt as expiredAt,
                    uc.coupon.amount as amount,
                    uc.coupon.couponType as couponType,
                    uc.coupon.minOrderAmount as minOrderAmount,
                    p.name as placeName,
                    f.url as placeImageUrl,
                    p.id as placeId
             from UserCoupon uc
             join Places p on uc.coupon.place.id = p.id
             join File f on f.id = (
                 select min(f2.id)
                 from File f2
                 where f2.domainFileId = p.id
             )
             where uc.user.id = :userId
             and uc.isUsed = false
             """)
    List<UserCouponProjection> findAllByUserIdAndCouponUsableCoupon(Long userId);

    @Query("""
            select uc.user.id as userId,uc.isUsed as isUsed,
                    uc.coupon.couponName as couponName,
                    uc.coupon.expiredAt as expiredAt,
                    uc.coupon.amount as amount,
                    uc.coupon.couponType as couponType,
                    uc.coupon.minOrderAmount as minOrderAmount,
                    p.name as placeName,
                    f.url as placeImageUrl,
                    p.id as placeId
             from UserCoupon uc
             join Places p on uc.coupon.place.id = p.id
             join File f on f.id = (
                 select min(f2.id)
                 from File f2
                 where f2.domainFileId = p.id
             )
             where uc.user.id = :userId""")
    List<UserCouponProjection> findAllByUserIdAndCouponAll(Long userId);

    @Query("""
            select uc.coupon
            from UserCoupon uc
            where uc.user.id = :userId
            and uc.isUsed = false
            and uc.coupon.expiredAt > current_timestamp
            and uc.coupon.place.id = :placeId
            """)
    List<CouponEntity> findUsableCouponsByUserIdAndPlaceId(Long userId, Long placeId);

    @Query("SELECT uc FROM UserCouponEntity uc " +
            "LEFT JOIN FETCH uc.coupon c " +
            "LEFT JOIN FETCH uc.user u " +
            "LEFT JOIN FETCH uc.id " +
            "WHERE u.id = :userId")
    List<UserCouponEntity> findAllWithCouponByUserId(@Param("userId") Long userId);

    @Query("SELECT " +
            "c.id as couponId, " +
            "c.couponName as couponName, " +
            "c.couponCode as couponCode, " +
            "c.amount as amount, " +
            "c.couponType as couponType, " +
            "uc.isUsed as isUsed, " +
            "uc.issuedAt as issuedAt, " +
            "c.expiredAt as expiredAt " +
            "FROM UserCouponEntity uc " +
            "JOIN uc.coupon c " +
            "WHERE uc.user.id = :userId")
    List<CouponIssuedProjection> findCouponsByUserId(Long userId);
}
