// src/main/java/daewoo/team5/hotelreservation/domain/place/review/repository/ReviewRepository.java
package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponseDto;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewProjection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}