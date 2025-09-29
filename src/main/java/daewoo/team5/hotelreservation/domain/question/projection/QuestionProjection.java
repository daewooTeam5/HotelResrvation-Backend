package daewoo.team5.hotelreservation.domain.question.projection;

import java.time.LocalDateTime;

public interface QuestionProjection {
    Long getId();
    String getTitle();
    String getContent();
    String getAnswer();
    LocalDateTime getCreatedAt();

    // 유저 정보
    Long getUser_id();
    String getUser_name();

    // 숙소 정보
    Long getPlace_id();
    String getPlace_name();
}
