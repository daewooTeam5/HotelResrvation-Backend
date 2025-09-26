package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.payment.projection.RoomInfoProjection;
import daewoo.team5.hotelreservation.domain.place.entity.Room;

import daewoo.team5.hotelreservation.domain.place.projection.AdminRoomInfoProjection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE room r SET r.status = :status WHERE r.id = :roomId")
    int updateRoomStatus(Long roomId, String status);

    List<AdminRoomInfoProjection> findByPlace_Id(Long placeId);

    @Query("""
                SELECT 
                    r.id AS id,
                    CONCAT(r.roomType, r.bedType) AS roomName,
                    MIN(COALESCE(d.availableRoom, r.capacityRoom)) AS availableCount,
                    r.place.id AS placeId,
                    r.price AS price,
                    COALESCE(dc.discountValue, 0) AS discountPercent,
                    FUNCTION('ROUND', r.price - r.price * COALESCE(dc.discountValue, 0) / 100, 0) AS finalPrice
                FROM room r
                LEFT JOIN DailyPlaceReservation d
                    ON d.date BETWEEN :startDate AND :endDate
                LEFT JOIN Discount dc
                    ON r.place.id = dc.place.id
                WHERE r.place.id = :placeId
                GROUP BY r.id, r.roomType, r.bedType, r.place.id, r.price, dc.discountValue
                HAVING MIN(COALESCE(d.availableRoom, r.capacityRoom)) > 0
            """)
    List<RoomInfoProjection> findPlacesInRoomAvailability(
            @Param("placeId") Long placeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(
            value = """
                        SELECT 
                            r.id AS id,
                            CONCAT(r.room_type, r.bed_type) AS roomName,
                            r.place_id AS placeId,
                            r.price AS price,
                            COALESCE(dc.discount_value, 0) AS discountPercent,
                            ROUND(r.price - r.price * COALESCE(dc.discount_value, 0) / 100, 0) AS finalPrice,
                            MIN(COALESCE(d.available_room, r.capacity_room)) AS availableCount
                        FROM room r
                        LEFT JOIN daily_place_reservation d 
                            ON d.date BETWEEN :startDate AND :endDate
                        LEFT JOIN discount dc
                            ON r.place_id = dc.place_id
                        WHERE r.id = :roomId
                        GROUP BY r.id, r.place_id, r.price, dc.discount_value
                        HAVING MIN(COALESCE(d.available_room, r.capacity_room)) > 0
                    """,
            nativeQuery = true
    )
    Optional<RoomInfoProjection> findRoomAvailability(
            @Param("roomId") Long roomId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    // ownerId로 해당 소유자의 모든 객실 유형 조회
    @Query("SELECT r FROM room r " +
            "JOIN r.place p " +
            "WHERE p.owner.id = :ownerId")
    List<Room> findAllByOwnerId(@Param("ownerId") Long ownerId);

    // ownerId + roomId 단건 조회 (권한 체크용)
    @Query("SELECT r FROM room r " +
            "JOIN r.place p " +
            "WHERE r.id = :roomId " +
            "AND p.owner.id = :ownerId")
    Optional<Room> findByIdAndOwnerId(@Param("roomId") Long roomId,
                                      @Param("ownerId") Long ownerId);


}
