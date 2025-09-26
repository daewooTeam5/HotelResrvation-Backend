package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StayDurationDTO {
    private double avgStayDuration; // 이번 달 평균 숙박 일수
    private double growthRate;      // 전월 대비 증감률 (%)
}