package daewoo.team5.hotelreservation.domain.place.review.dto;

import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

// 주석: 리뷰 정보를 클라이언트에게 응답할 때 사용하는 DTO입니다.
@Getter
public class ReviewResponse {
    private final Long reviewId;
    private final String userName;
    private final Integer rating;
    private final String comment;
    private final LocalDateTime createdAt;

    public ReviewResponse(Review review) {
        this.reviewId = review.getReviewId();
        this.userName = review.getUser().getName(); // 사용자 이름
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
    }
}