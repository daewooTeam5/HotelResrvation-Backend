package daewoo.team5.hotelreservation.domain.shoppingcart.repository;

import daewoo.team5.hotelreservation.domain.shoppingcart.entity.ShoppingCart;
import daewoo.team5.hotelreservation.domain.shoppingcart.projection.CartProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser_IdAndRoom_IdAndStartDateAndEndDate(
            Long userId,
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );

    void deleteByUser_IdAndRoom_IdAndStartDateAndEndDate(
            Long userId,
            Long roomId,
            LocalDate checkIn,
            LocalDate checkOut
    );

    @Query("SELECT COALESCE(SUM(sc.quantity), 0) FROM ShoppingCart sc WHERE sc.user.id = :userId")
    Integer countByUser_Id(@Param("userId") Long userId);

    @Query(value = """
    SELECT 
        sc.id AS cartId,
        p.name AS placeName,
        r.room_type AS roomName,
        sc.check_in AS startDate,
        sc.check_out AS endDate,
        p.check_in AS checkIn,
        p.check_out AS checkOut,
        r.price AS price,
        sc.quantity AS quantity,
        r.capacity_people AS capacityPeople,
        pa.sido AS sido,
        pa.sigungu AS sigungu,
        pa.road_name AS roadName,
        CONCAT(pa.sido, ' ', pa.sigungu, ' ', pa.road_name, ' ', pa.detail_address) AS detailAddress,
        (
            SELECT f.url
            FROM file f
            WHERE f.domain = 'room' 
              AND f.domain_file_id = r.id
            ORDER BY f.id ASC
            LIMIT 1
        ) AS fileUrl
    FROM shopping sc
    JOIN room r ON sc.room_id = r.id
    JOIN places p ON r.place_id = p.id
    JOIN place_address pa ON pa.place_id = p.id
    WHERE sc.user_id = :userId
""", nativeQuery = true)
    List<CartProjection> findCartItemsByUserId(@Param("userId") Long userId);
}
