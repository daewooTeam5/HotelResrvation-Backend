// src/main/java/daewoo/team5/hotelreservation/domain/place/review/repository/ReviewCommentRepository.java
package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
}