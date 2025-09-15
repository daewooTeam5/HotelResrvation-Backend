package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceDetailProjection;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.place.projection.RoomInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Places, Long> {

    @Query(value = """
            
                SELECT 
                p.id AS id,
                p.name AS name,
                p.avg_rating AS avg_rating,
                pa.sido AS sido,
                (
                    SELECT f.url
                    FROM file f
                    WHERE f.domain = 'place'
                      AND f.domain_file_id = p.id
                    LIMIT 1
                ) AS fileUrl,
                (
                    SELECT MIN(r.price)
                    FROM rooms r
                    WHERE r.place_id = p.id
                ) AS price
            FROM places p
            INNER JOIN place_address pa ON p.id = pa.place_id
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM places p
                    INNER JOIN place_address pa ON p.id = pa.place_id
                    """,
            nativeQuery = true)
    Page<PlaceItemInfomation> findAllPlaceInfo(Pageable pageable);

    @Query(value = """
            WITH RECURSIVE date_range AS (
                SELECT CAST(:checkIn AS DATE) AS date
                UNION ALL
                SELECT DATE_ADD(date, INTERVAL 1 DAY)
                FROM date_range
                WHERE date < CAST(:checkOut AS DATE)
            )
            SELECT
                p.id AS id,
                p.name AS name,
                p.avg_rating AS avgRating,
                pa.sido AS sido,
                pc.name AS categoryName,
                (
                    SELECT f.url
                    FROM file f
                    WHERE f.domain = 'place' 
                      AND f.domain_file_id = p.id 
                      AND f.filetype = 'image'
                    LIMIT 1
                ) AS fileUrl,
                (
                    SELECT MIN(r.price)
                    FROM rooms r
                    WHERE r.place_id = p.id
                      AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :rooms)
                      AND NOT EXISTS (
                          SELECT 1
                          FROM date_range d
                          LEFT JOIN daily_place_reservation dpr
                                 ON dpr.room_id = r.id 
                                AND DATE(dpr.date) = d.date
                          WHERE COALESCE(dpr.available_room, r.capacity_room) <= 0
                      )
                ) AS price
            FROM places p
            INNER JOIN place_address pa ON p.id = pa.place_id
            INNER JOIN place_category pc ON p.category_id = pc.id
            WHERE (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
              AND EXISTS (
                  SELECT 1
                  FROM rooms r
                  WHERE r.place_id = p.id
                    AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :rooms)
              )
              AND NOT EXISTS (
                  SELECT 1
                  FROM date_range d
                  JOIN rooms r ON r.place_id = p.id
                  LEFT JOIN daily_place_reservation dpr
                         ON r.id = dpr.room_id 
                        AND DATE(dpr.date) = d.date
                  GROUP BY d.date
                  HAVING
                      SUM(COALESCE(dpr.available_room, r.capacity_room)) < :rooms
                      OR SUM(COALESCE(dpr.available_room, r.capacity_room) * r.capacity_people) < :people
                      OR COUNT(r.id) = 0
              )
              AND (:placeCategory IS NULL OR pc.name = :placeCategory)
              AND (:minRating IS NULL OR p.avg_rating >= :minRating)
              AND (:minPrice IS NULL OR (
                    SELECT MIN(r.price) 
                    FROM rooms r 
                    WHERE r.place_id = p.id
                      AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :rooms)
                ) >= :minPrice)
              AND (:maxPrice IS NULL OR (
                    SELECT MIN(r.price) 
                    FROM rooms r 
                    WHERE r.place_id = p.id
                      AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :rooms)
                ) <= :maxPrice)
            """,
            countQuery = """
                        SELECT COUNT(*)
                        FROM places p
                        INNER JOIN place_address pa ON p.id = pa.place_id
                        INNER JOIN place_category pc ON p.category_id = pc.id
                    """,
            nativeQuery = true)
    Page<PlaceItemInfomation> findAllSearchPlaceInfo(
            @Param("name") String name,
            @Param("checkIn") String checkIn,
            @Param("checkOut") String checkOut,
            @Param("people") int people,
            @Param("rooms") int rooms,
            @Param("placeCategory") String placeCategory,
            @Param("minRating") Double minRating,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    @Query(value = """
            SELECT 
                p.id AS id,
                p.name AS name,
                p.description AS description,
                p.avg_rating AS avgRating,
                pa.sido AS sido,
                pa.sigungu AS sigungu,
                pa.road_name AS roadName,
                pa.detail_address AS detailAddress
            FROM places p
            INNER JOIN place_address pa ON p.id = pa.place_id
            WHERE p.id = :placeId
            """, nativeQuery = true)
    PlaceDetailProjection findPlaceDetail(@Param("placeId") Long placeId);

    // 이미지 리스트
    @Query(value = """
            SELECT f.url
            FROM file f
            WHERE f.domain = 'place'
              AND f.domain_file_id = :placeId
              AND f.filetype = 'image'
            """, nativeQuery = true)
    List<String> findPlaceImages(@Param("placeId") Long placeId);

    // 객실 상세
    @Query(value = """
            SELECT 
                r.room_type AS roomType,
                r.bed_type AS bedType,
                r.capacity_people AS capacityPeople,
                r.capacity_room AS capacityRoom,
                r.price AS price,
                r.status AS status
            FROM rooms r
            WHERE r.place_id = :placeId
            """, nativeQuery = true)
    List<RoomInfo> findRoomsByPlace(@Param("placeId") Long placeId);
}



