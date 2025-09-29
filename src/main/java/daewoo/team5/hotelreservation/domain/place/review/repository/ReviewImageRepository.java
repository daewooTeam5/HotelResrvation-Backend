// src/main/java/daewoo/team5/hotelreservation/domain/place/review/repository/ReviewImageRepository.java

package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewImage;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewImageProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    @Query("SELECT i.id as id, i.imageUrl as imageUrl, r.reviewId as review_reviewId " +
            "FROM ReviewImage i " +
            "JOIN i.review r " +
            "WHERE r.user.id = :userId")
    List<ReviewImageProjection> findReviewImagesByUserId(Long userId);
}