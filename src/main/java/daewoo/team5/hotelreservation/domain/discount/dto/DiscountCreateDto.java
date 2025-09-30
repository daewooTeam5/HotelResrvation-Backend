package daewoo.team5.hotelreservation.domain.discount.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DiscountCreateDto {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountValue;
    private Integer maxDiscountAmount;
}