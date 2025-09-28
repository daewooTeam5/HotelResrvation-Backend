package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyAvailabilityDTO {

    private LocalDate date; // 날짜
    private List<RoomAvailabilityDTO> rooms; // 객실 유형별 가용 현황
}