package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityDTO {

    private LocalDate date;   // 해당 날짜
    private String roomType;  // 객실 유형 (스탠다드, 스위트 등)
    private int available;    // 남은 객실 수
    private int total;        // 전체 객실 수
}