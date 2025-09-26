package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//rooms -> room으로 수정

@Repository
public interface PlaceRepository extends JpaRepository<Places, Long> {

    @Query(value = """
            SELECT
                p.id,
                p.name,
                p.avg_rating AS avgRating,
                pa.sido,
                pc.name AS categoryName,
                MIN(r.price) AS originalPrice, -- (1) 기존 가격
                MIN(f.url) AS fileUrl,
                MAX(d.discount_value) AS discountValue, -- (2) 할인 금액
                MIN(r.price) - COALESCE(MAX(d.discount_value), 0) AS finalPrice, -- (3) 최종 가격
                CASE
                    WHEN :userId IS NULL THEN 0
                    WHEN EXISTS (
                        SELECT 1
                        FROM wishlist w
                        WHERE w.place_id = p.id AND w.user_id = :userId
                    )
                    THEN 1
                    ELSE 0
                END AS isLiked
            FROM places p
            INNER JOIN place_address pa ON p.id = pa.place_id
            INNER JOIN place_category pc ON p.category_id = pc.id
            INNER JOIN room r ON r.place_id = p.id
            LEFT JOIN file f ON f.domain = 'place' AND f.domain_file_id = p.id AND f.filetype = 'image'
            /* ===== [추가된 부분 시작] ===== */
            LEFT JOIN discount d ON p.id = d.place_id
                AND d.start_date <= CAST(:checkOut AS DATE) -- 검색 종료일 이전에 할인이 시작되고
                AND d.end_date >= CAST(:checkIn AS DATE)   -- 검색 시작일 이후에 할인이 종료되는 경우
            /* ===== [추가된 부분 끝] ===== */
            WHERE
                  (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
                AND (:address IS NULL OR pa.sido = :address)
                AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :room)
                AND r.price BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 999999999)
                AND COALESCE(
                        (SELECT MIN(dpr.available_room)
                         FROM daily_place_reservation dpr
                         WHERE dpr.room_id = r.id
                           AND dpr.date BETWEEN CAST(:checkIn AS DATE) AND DATE_SUB(CAST(:checkOut AS DATE), INTERVAL 1 DAY)
                        ),
                        r.capacity_room
                    ) >= :room
                AND (:placeCategory IS NULL OR pc.name = :placeCategory)
                AND (:minRating IS NULL OR p.avg_rating >= :minRating)
                AND NOT EXISTS (
                    SELECT 1
                    FROM (
                        SELECT CAST(:checkIn AS DATE) AS date
                        UNION ALL
                        SELECT DATE_ADD(date, INTERVAL 1 DAY) 
                        FROM (SELECT CAST(:checkIn AS DATE) AS date) AS t
                        WHERE DATE_ADD(date, INTERVAL 1 DAY) < CAST(:checkOut AS DATE)
                    ) AS date_range
                    JOIN daily_place_reservation dpr
                         ON dpr.room_id = r.id
                        AND dpr.date = date_range.date
                    WHERE dpr.available_room <= 0
                )
            GROUP BY p.id, p.name, p.avg_rating, pa.sido, pc.name
            """,
            countQuery = """
                    WITH RECURSIVE date_range AS (
                        SELECT CAST(:checkIn AS DATE) AS date
                        UNION ALL
                        SELECT DATE_ADD(date, INTERVAL 1 DAY) 
                        FROM date_range 
                        WHERE date < CAST(:checkOut AS DATE)
                    )
                    SELECT COUNT(DISTINCT p.id)
                    FROM places p
                    INNER JOIN place_address pa ON p.id = pa.place_id
                    INNER JOIN place_category pc ON p.category_id = pc.id
                    INNER JOIN room r ON r.place_id = p.id
                    WHERE
                        (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
                        AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :room)
                        AND r.price BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 999999999)
                        AND (:placeCategory IS NULL OR pc.name = :placeCategory)
                        AND (:minRating IS NULL OR p.avg_rating >= :minRating)
                        AND COALESCE(
                                (SELECT MIN(dpr.available_room)
                                 FROM daily_place_reservation dpr
                                 WHERE dpr.room_id = r.id
                                   AND dpr.date BETWEEN CAST(:checkIn AS DATE) AND DATE_SUB(CAST(:checkOut AS DATE), INTERVAL 1 DAY)
                                ),
                                r.capacity_room
                            ) >= :room
                        AND NOT EXISTS (
                            SELECT 1
                            FROM date_range d
                            JOIN daily_place_reservation dpr 
                                 ON dpr.room_id = r.id 
                                AND dpr.date = d.date
                            WHERE dpr.available_room <= 0
                        )
                    """,
            nativeQuery = true)
    Page<PlaceItemInfomation> findAllSearchPlaceInfo(
            @Param("name") String name,
            @Param("checkIn") String checkIn,
            @Param("checkOut") String checkOut,
            @Param("people") int people,
            @Param("room") int room,
            @Param("placeCategory") String placeCategory,
            @Param("minRating") Double minRating,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("userId") Long userId,
            @Param("address") String address,
            Pageable pageable
    );

    @Query(value = """
            SELECT 
                p.id AS id,
                p.name AS name,
                p.description AS description,
                p.avg_rating AS avgRating,
                p.check_in AS checkIn,
                p.check_out AS checkOut,
                pa.sido AS sido,
                pa.sigungu AS sigungu,
                pa.road_name AS roadName,
                pa.detail_address AS detailAddress
            FROM places p
            INNER JOIN place_address pa ON p.id = pa.place_id
            WHERE p.id = :placeId
            """, nativeQuery = true)
    Optional<PlaceDetailProjection> findPlaceDetail(@Param("placeId") Long placeId);

    // 이미지 리스트
    @Query(value = """
            SELECT f.url
              FROM file f
              WHERE f.domain = 'place'
                AND f.domain_file_id = :placeId
                AND f.filetype = 'image'
            
              UNION ALL
            
              SELECT f.url
              FROM file f
              JOIN room r ON f.domain = 'room' AND f.domain_file_id = r.id
              WHERE r.place_id = :placeId
                AND f.filetype = 'image'
            """, nativeQuery = true)
    List<String> findPlaceImages(@Param("placeId") Long placeId);

    @Query(value = """
                SELECT
                  r.id AS roomId,
                  r.room_type AS roomType,
                  r.bed_type AS bedType,
                  r.capacity_people AS capacityPeople,
                  r.capacity_room AS capacityRoom,
                  r.price AS price,
                  r.status AS status,
                  COALESCE(MIN(dpr.available_room), r.capacity_room) AS availableRoom,
                  GROUP_CONCAT(f.url SEPARATOR ',') AS images
              FROM room r
              LEFT JOIN file f
                     ON f.domain = 'room'
                    AND f.domain_file_id = r.id
                    AND f.filetype = 'image'
              LEFT JOIN daily_place_reservation dpr
                     ON dpr.room_id = r.id
                    AND dpr.date BETWEEN :startDate AND :endDate
              WHERE r.place_id = :placeId
              GROUP BY r.id, r.room_type, r.bed_type, r.capacity_people,
                       r.capacity_room, r.price, r.status;
            """, nativeQuery = true)
    List<RoomInfo> findRoomsByPlace(
            @Param("placeId") Long placeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query(value = """
            SELECT s.id AS id,
                   s.name AS name,
                   s.icon AS icon
            FROM place_amenity ps
            INNER JOIN amenity s ON ps.amenity_id = s.id
            WHERE ps.place_id = :placeId
            """, nativeQuery = true)
    List<PlaceServiceProjection> findPlaceServices(@Param("placeId") Long placeId);

    Optional<Places> findByOwnerId(Long ownerId);

    @Query("""
        SELECT p.id AS id,
               p.name AS name,
               u.id AS ownerId,
               u.name AS ownerName,
               a.sido AS sido,
               a.sigungu AS sigungu,
               c.name AS categoryName,
               p.status AS status
        FROM Places p
        JOIN p.owner u
        JOIN PlaceAddress a ON a.place = p
        JOIN p.category c
        WHERE (:sido IS NULL OR a.sido = :sido)
          AND (:sigungu IS NULL OR a.sigungu = :sigungu)
          AND (:approvalStatus IS NULL OR p.status = :approvalStatus)
          AND (:ownerName IS NULL OR u.name LIKE %:ownerName%)
          AND (:placeName IS NULL OR p.name LIKE %:placeName%)
        """)
    Page<AdminPlaceProjection> searchAdminPlaces(
            @Param("sido") String sido,
            @Param("sigungu") String sigungu,
            @Param("approvalStatus") Places.Status approvalStatus,
            @Param("ownerName") String ownerName,
            @Param("placeName") String placeName,
            Pageable pageable
    );
}




