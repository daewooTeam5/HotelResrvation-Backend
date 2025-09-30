package daewoo.team5.hotelreservation.domain.discount.dto;

import daewoo.team5.hotelreservation.domain.payment.entity.DiscountEntity;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiscountResponseDto {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer discountValue;
    private Long placeId;
    private Integer maxDiscountAmount;

    public DiscountResponseDto(DiscountEntity entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.startDate = entity.getStartDate();
        this.endDate = entity.getEndDate();
        this.discountValue = entity.getDiscountValue();
        this.placeId = entity.getPlace().getId();
        this.maxDiscountAmount = entity.getMaxDiscountAmount();
    }
}