package daewoo.team5.hotelreservation.domain.wishlist.repository;

import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.wishlist.entity.WishList;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WishListRepository extends JpaRepository<WishList, Long> {
    boolean existsByUserIdAndPlaceId(Long userId, Long placeId);

    void deleteByUserIdAndPlaceId(Long userId, Long placeId);

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
        1 AS isLiked
    FROM places p
    INNER JOIN place_address pa ON p.id = pa.place_id
    INNER JOIN place_category pc ON p.category_id = pc.id
    INNER JOIN room r ON r.place_id = p.id
    LEFT JOIN file f 
           ON f.domain = 'place' 
          AND f.domain_file_id = p.id 
          AND f.filetype = 'image'
    /* ===== 할인 조건 추가 ===== */
    LEFT JOIN discount d 
           ON p.id = d.place_id
          AND d.start_date <= CAST(:checkOut AS DATE) 
          AND d.end_date   >= CAST(:checkIn AS DATE)
    /* ======================== */
    INNER JOIN wishlist w ON w.place_id = p.id AND w.user_id = :userId
    WHERE
        (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
        AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :room)
        AND r.price BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 999999999)
        AND (:placeCategory IS NULL OR pc.name = :placeCategory)
        AND (:minRating IS NULL OR p.avg_rating >= :minRating)
        AND NOT EXISTS (
            SELECT 1
            FROM (
                SELECT DATE_ADD(CAST(:checkIn AS DATE), INTERVAL n.n DAY) AS date
                FROM (
                    SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                    SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
                ) n
                WHERE DATE_ADD(CAST(:checkIn AS DATE), INTERVAL n.n DAY) < CAST(:checkOut AS DATE)
            ) d_range
            JOIN daily_place_reservation dpr
                 ON dpr.room_id = r.id
                AND dpr.date = d_range.date
            WHERE dpr.available_room <= 0
        )
    GROUP BY p.id, p.name, p.avg_rating, pa.sido, pc.name
    """,
            countQuery = """
    SELECT COUNT(DISTINCT p.id)
    FROM places p
    INNER JOIN place_address pa ON p.id = pa.place_id
    INNER JOIN place_category pc ON p.category_id = pc.id
    INNER JOIN room r ON r.place_id = p.id
    INNER JOIN wishlist w ON w.place_id = p.id AND w.user_id = :userId
    WHERE
        (:name IS NULL OR p.name LIKE CONCAT('%', :name, '%'))
        AND r.capacity_people >= CEIL(CAST(:people AS DECIMAL) / :room)
        AND r.price BETWEEN COALESCE(:minPrice, 0) AND COALESCE(:maxPrice, 999999999)
        AND (:placeCategory IS NULL OR pc.name = :placeCategory)
        AND (:minRating IS NULL OR p.avg_rating >= :minRating)
        AND NOT EXISTS (
            SELECT 1
            FROM (
                SELECT DATE_ADD(CAST(:checkIn AS DATE), INTERVAL n.n DAY) AS date
                FROM (
                    SELECT 0 n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL
                    SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9
                ) n
                WHERE DATE_ADD(CAST(:checkIn AS DATE), INTERVAL n.n DAY) < CAST(:checkOut AS DATE)
            ) d_range
            JOIN daily_place_reservation dpr 
                 ON dpr.room_id = r.id 
                AND dpr.date = d_range.date
            WHERE dpr.available_room <= 0
        )
    """,
            nativeQuery = true)
    Page<PlaceItemInfomation> findUserWishList(
            @Param("userId") Long userId,
            @Param("name") String name,
            @Param("checkIn") String checkIn,
            @Param("checkOut") String checkOut,
            @Param("people") int people,
            @Param("room") int room,
            @Param("placeCategory") String placeCategory,
            @Param("minRating") Double minRating,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}
