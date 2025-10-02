package daewoo.team5.hotelreservation.domain.place.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyReviewResponse {
    private Long reviewId;
    private PlaceInfo place;
    private Integer rating;
    private String comment;
    private List<String> imageUrls;
    private OwnerCommentInfo ownerComment;
    private LocalDateTime createdAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceInfo {
        private Long placeId;
        private String placeName;
        private String categoryName;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OwnerCommentInfo {
        private Long commentId;
        private String comment;
        private String ownerName;
        private LocalDateTime createdAt;
    }
}
