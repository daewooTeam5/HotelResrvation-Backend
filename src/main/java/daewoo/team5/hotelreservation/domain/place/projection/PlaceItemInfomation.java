package daewoo.team5.hotelreservation.domain.place.projection;

import java.math.BigDecimal;

public interface PlaceItemInfomation {
    Long getId();
    String getName();
    BigDecimal getOriginalPrice(); // 기존 price -> originalPrice 로 변경
    BigDecimal getFinalPrice();    // 할인 적용된 최종 가격
    Integer getDiscountValue();
    String getSido();
    String getFileUrl();
    BigDecimal getAvgRating();
    String getCategoryName();
    Integer getIsLiked();
//    Integer getStar();|
}