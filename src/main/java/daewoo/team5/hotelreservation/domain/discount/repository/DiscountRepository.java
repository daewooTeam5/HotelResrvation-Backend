package daewoo.team5.hotelreservation.domain.discount.repository;

import daewoo.team5.hotelreservation.domain.payment.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<DiscountEntity, Long> {
    List<DiscountEntity> findByPlaceId(Long placeId);

    @Query("SELECT r.resevStart, COUNT(r.reservationId) " + // [!code ++]
            "FROM Reservation r " + // [!code ++]
            "WHERE r.room.place.id = :placeId " + // [!code ++]
            "AND r.fixedDiscountAmount > 0 " + // [!code ++]
            "AND r.resevStart BETWEEN :startDate AND :endDate " + // [!code ++]
            "GROUP BY r.resevStart") // [!code ++]
    List<Object[]> findDiscountUsageByDate(@Param("placeId") Long placeId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate); // [!code ++]
}