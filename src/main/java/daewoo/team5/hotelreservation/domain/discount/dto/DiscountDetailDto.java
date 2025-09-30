package daewoo.team5.hotelreservation.domain.discount.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class DiscountDetailDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountValue;
    private Integer maxDiscountAmount;
    private long usageCount;
    private long totalDiscountAmount;
    private Map<LocalDate, Long> dailyUsage;
}