// src/main/java/daewoo/team5/hotelreservation/domain/place/review/repository/ReviewCommentRepository.java
package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewComment;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewCommentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    @Query("SELECT c.id as id, c.comment as comment, r.reviewId as review_reviewId, " +
            "u.id as user_id, u.name as user_name " +
            "FROM ReviewComment c " +
            "JOIN c.review r " +
            "JOIN c.user u " +
            "WHERE r.user.id = :userId")
    List<ReviewCommentProjection> findReviewCommentsByUserId(Long userId);
}