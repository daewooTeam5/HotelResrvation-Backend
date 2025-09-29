package daewoo.team5.hotelreservation.domain.place.review.dto;

import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.entity.ReviewImage;

import java.util.List;

public record ReviewDto(
        Long reviewId,
        String comment,
        Integer rating,
        String userName,
        List<String> imageUrls,
        String ownerComment
) {
    public static ReviewDto fromEntity(Review review) {
        return new ReviewDto(
                review.getReviewId(),
                review.getComment(),
                review.getRating(),
                review.getUser().getName(), // Users 엔티티에 name 있다고 가정
                review.getImages().stream().map(ReviewImage::getImageUrl).toList(),
                review.getCommentByOwner() != null ? review.getCommentByOwner().getComment() : null
        );
    }
}
