package daewoo.team5.hotelreservation.domain.payment.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
import daewoo.team5.hotelreservation.domain.payment.projection.PointProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


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
}
