package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RecentReviewDTO {
    private Long reviewId;
    private String userName;   // users.name
    private int rating;        // reviews.rating
    private String comment;    // reviews.comment
    private LocalDateTime createdAt;
}