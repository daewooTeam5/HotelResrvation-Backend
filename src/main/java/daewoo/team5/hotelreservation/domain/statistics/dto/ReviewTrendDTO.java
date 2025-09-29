package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewTrendDTO {
    private String label; // x축 (날짜/주/월/연도)
    private long count;   // 리뷰 수
}