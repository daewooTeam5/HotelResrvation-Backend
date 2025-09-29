package daewoo.team5.hotelreservation.domain.payment.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
import daewoo.team5.hotelreservation.domain.payment.projection.PointProjection;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.payment.projection.PointHistorySummaryProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity,Long> {
    @Query("SELECT p.id as id, " +
            "p.userId as userId, " +
            "p.type as type, " +
            "p.amount as amount, " +
            "p.balanceAfter as balanceAfter, " +
            "p.expireAt as expireAt, " +
            "p.createdAt as createdAt, " +
            "r.reservationId as reservationId " +
            "FROM PointHistory p " +
            "LEFT JOIN p.reservation r " +
            "WHERE p.userId = :userId " +
            "ORDER BY p.createdAt DESC")
    List<PointProjection> findPointsByUserId(Long userId);

    Optional<PointHistoryEntity> findByReservationAndType(Reservation reservation, PointHistoryEntity.PointType type);

    List<PointHistoryEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("select " +
            " ph.id as id, ph.type as type,ph.description as description, ph.amount as amount, ph.balanceAfter as balanceAfter, ph.expireAt as expireAt, ph.createdAt as createdAt, " +
            " r.reservationId as reservationId, r.orderId as orderId, p.name as hotelName, ro.roomType as roomType, ro.bedType as bedType " +
            " from PointHistory ph " +
            " left join ph.reservation r " +
            " left join r.room ro " +
            " left join ro.place p " +
            " where ph.user.id = :userId " +
            " order by ph.createdAt desc")
    List<PointHistorySummaryProjection> findSummaryByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

}
