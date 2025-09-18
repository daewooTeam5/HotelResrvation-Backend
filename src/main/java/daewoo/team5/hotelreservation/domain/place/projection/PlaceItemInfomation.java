package daewoo.team5.hotelreservation.domain.place.projection;

import java.math.BigDecimal;

public interface PlaceItemInfomation {
    Long getId();
    String getName();
    BigDecimal  getPrice();
    String getSido();
    String getFileUrl();
    BigDecimal getAvgRating();
    String getCategoryName();
//    Integer getStar();|
}