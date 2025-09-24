package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class StatisticsResponse {
    private String title;                 // 차트 이름
    private String period;                // daily, weekly, monthly, yearly
    private List<String> labels;          // X축 라벨 (예: 날짜, 월)
    private List<Number> values;          // Y축 값
    private Map<String, Object> extra;    // 부가 데이터 (필요시)
}