// src/main/java/daewoo/team5/hotelreservation/domain/place/review/dto/MyReviewResponseDto.java
package daewoo.team5.hotelreservation.domain.place.review.dto;

import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.projection.ReviewProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class MyReviewResponseDTO {
    private Long reviewId;
    private String comment;
    private Integer rating;
    private Long placeId;
    private Long reservationId;
    private Long userId;

    public MyReviewResponseDTO(ReviewProjection projection) {
        this.reviewId = projection.getReviewId();
        this.comment = projection.getComment();
        this.rating = projection.getRating();
        this.placeId = projection.getPlace_id();
        this.reservationId = projection.getReservation_reservationId();
        this.userId = projection.getUser_id();
    }
}