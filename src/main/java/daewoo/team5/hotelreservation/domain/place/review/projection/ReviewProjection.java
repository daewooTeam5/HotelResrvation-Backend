package daewoo.team5.hotelreservation.domain.place.review.projection;

import java.time.LocalDateTime;

public interface ReviewProjection {
    Long getReviewId();
    Integer getRating();
    String getComment();
    LocalDateTime getCreatedAt();

    // 유저
    Long getUser_id();
    String getUser_name();

    // 숙소
    Long getPlace_id();
    String getPlace_name();

    // 예약
    Long getReservation_reservationId();
}
