// daewooteam5/hotelreservation-backend/HotelReservation-Backend-feature-review3/src/main/java/daewoo/team5/hotelreservation/domain/place/review/dto/ReviewResponse.java
package daewoo.team5.hotelreservation.domain.place.review.dto;

import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewComment;
import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewImage;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReviewResponse {
    private final Long reviewId;
    private final String userName;
    private final Long userId; // <-- [추가] 사용자 ID 필드
    private final Integer rating;
    private final String comment;
    private final LocalDateTime createdAt;
    private final List<String> imageUrls;
    private final ReviewCommentDto commentByOwner;

    // ===== ✅ 새로 추가된 필드 =====
    private final String roomType; // 객실 타입
    private final long nights;     // 숙박 일수

    public ReviewResponse(Review review) {
        this.reviewId = review.getReviewId();
        this.userName = review.getUser().getName();
        this.userId = review.getUser().getId(); // <-- [추가] 생성자에서 userId 값 할당
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();
        this.imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .collect(Collectors.toList());
        this.commentByOwner = review.getCommentByOwner() != null ? new ReviewCommentDto(review.getCommentByOwner()) : null;

        // ===== ✅ 새로운 필드 데이터 할당 =====
        this.roomType = review.getReservation().getRoom().getRoomType();
        // 주석: 체크인 날짜와 체크아웃 날짜의 차이를 계산하여 숙박 일수를 구합니다.
        this.nights = ChronoUnit.DAYS.between(review.getReservation().getResevStart(), review.getReservation().getResevEnd());
    }

    @Getter
    private static class ReviewCommentDto {
        private final String comment;
        private final String managerName;
        private final LocalDateTime createdAt;

        public ReviewCommentDto(ReviewComment comment) {
            this.comment = comment.getComment();
            this.managerName = comment.getUser().getName();
            this.createdAt = comment.getCreatedAt();
        }
    }
}