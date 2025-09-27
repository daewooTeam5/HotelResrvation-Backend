package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewSummaryDTO {
    private double avgRating;
    private long totalReviews;
    private double reviewRate; // 예약 대비 리뷰 작성률
}