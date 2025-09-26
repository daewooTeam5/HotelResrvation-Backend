package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CancelRateDTO {
    private double cancelRate;     // 이번 달 취소율 (%)
    private double growthRate;     // 전월 대비 증감률 (%)
}