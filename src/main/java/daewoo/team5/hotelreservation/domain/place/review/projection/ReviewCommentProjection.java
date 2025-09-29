package daewoo.team5.hotelreservation.domain.place.review.projection;

public interface ReviewCommentProjection {
    Long getId();
    String getComment();
    Long getReview_reviewId();

    // 작성자 정보
    Long getUser_id();
    String getUser_name();
}

