// src/main/java/daewoo/team5/hotelreservation/domain/place/review/repository/ReviewImageRepository.java

package daewoo.team5.hotelreservation.domain.place.review.repository;

import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}