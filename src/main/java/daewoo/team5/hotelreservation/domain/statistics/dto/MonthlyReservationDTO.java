package daewoo.team5.hotelreservation.domain.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MonthlyReservationDTO {
    private long thisMonthReservations;
    private double growthRate;
}