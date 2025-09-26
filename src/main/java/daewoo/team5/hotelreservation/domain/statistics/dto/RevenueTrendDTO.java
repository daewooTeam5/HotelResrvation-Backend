package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RevenueTrendDTO {
    private String label;   // 일자, 주차, 월, 연도 등
    private long revenue;   // 매출액
}