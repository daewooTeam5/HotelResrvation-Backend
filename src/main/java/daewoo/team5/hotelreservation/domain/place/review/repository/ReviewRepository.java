// src/main/java/daewoo/team5/hotelreservation/domain/place/review/repository/ReviewRepository.java
package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponseDto;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPlaceId(Long placeId, Sort sort);

    @Query("SELECT r FROM Review r JOIN FETCH r.place p JOIN FETCH p.owner WHERE r.reviewId = :id")
    Optional<Review> findByIdWithPlaceAndOwner(@Param("id") Long id);

    // ✅ [추가] 예약 ID로 리뷰 존재 여부를 확인하는 쿼리
    boolean existsByReservationReservationId(Long reservationId);

    // 특정 숙소 주인의 최근 리뷰 3개 (reviews → places → owner_id)
    List<Review> findTop3ByPlace_OwnerIdOrderByCreatedAtDesc(Long ownerId);

    @Query("SELECT new daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponseDto(" +
            "r.reviewId, u.name, u.role, r.comment, p.name, rc.comment) " +
            "FROM Review r " +
            "JOIN r.user u " +
            "JOIN r.place p " +
            "LEFT JOIN r.commentByOwner rc")
    List<ReviewResponseDto> findAllReviewsWithDetails();

    @Query("SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.images " +
            "LEFT JOIN FETCH r.commentByOwner " +
            "WHERE r.place.id = :placeId")
    List<Review> findAllByPlaceIdWithDetails(@Param("placeId") Long placeId);

    @Query("SELECT r.reviewId as reviewId, r.rating as rating, r.comment as comment, " +
            "r.createdAt as createdAt, u.id as user_id, u.name as user_name, " +
            "p.id as place_id, p.name as place_name, res.reservationId as reservation_reservationId " +
            "FROM Review r " +
            "JOIN r.user u " +
            "JOIN r.place p " +
            "JOIN r.reservation res " +
            "WHERE u.id = :userId")
    List<ReviewProjection> findReviewsByUserId(Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.place.owner.id = :ownerId")
    Double findAvgRatingByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.place.owner.id = :ownerId")
    long countByOwner(@Param("ownerId") Long ownerId);

    @Query("SELECT r.rating, COUNT(r) " +
            "FROM Review r " +
            "WHERE r.place.owner.id = :ownerId " +
            "AND r.createdAt BETWEEN :startDateTime AND :endDateTime " +
            "GROUP BY r.rating")
    List<Object[]> findRatingDistribution(@Param("ownerId") Long ownerId,
                                          @Param("startDateTime") LocalDateTime startDateTime,
                                          @Param("endDateTime") LocalDateTime endDateTime);

    // 일별
    @Query(value = "SELECT DATE(r.created_at) AS label, COUNT(*) " +
            "FROM reviews r " +
            "JOIN places p ON r.place_id = p.id " +
            "WHERE p.owner_id = :ownerId " +
            "AND r.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(r.created_at) " +
            "ORDER BY DATE(r.created_at)", nativeQuery = true)
    List<Object[]> countDailyReviews(@Param("ownerId") Long ownerId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate);

    // 주별 (연-주차)
    @Query(value = "SELECT DATE_FORMAT(r.created_at, '%x-%v') AS label, COUNT(*) " +
            "FROM reviews r " +
            "JOIN places p ON r.place_id = p.id " +
            "WHERE p.owner_id = :ownerId " +
            "AND r.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_FORMAT(r.created_at, '%x-%v') " +
            "ORDER BY label", nativeQuery = true)
    List<Object[]> countWeeklyReviews(@Param("ownerId") Long ownerId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

    // 월별
    @Query(value = "SELECT DATE_FORMAT(r.created_at, '%Y-%m') AS label, COUNT(*) " +
            "FROM reviews r " +
            "JOIN places p ON r.place_id = p.id " +
            "WHERE p.owner_id = :ownerId " +
            "AND r.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE_FORMAT(r.created_at, '%Y-%m') " +
            "ORDER BY label", nativeQuery = true)
    List<Object[]> countMonthlyReviews(@Param("ownerId") Long ownerId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    // 연도별
    @Query(value = "SELECT YEAR(r.created_at) AS label, COUNT(*) " +
            "FROM reviews r " +
            "JOIN places p ON r.place_id = p.id " +
            "WHERE p.owner_id = :ownerId " +
            "AND r.created_at BETWEEN :startDate AND :endDate " +
            "GROUP BY YEAR(r.created_at) " +
            "ORDER BY label", nativeQuery = true)
    List<Object[]> countYearlyReviews(@Param("ownerId") Long ownerId,
                                      @Param("startDate") LocalDate startDate,
                                      @Param("endDate") LocalDate endDate);

}