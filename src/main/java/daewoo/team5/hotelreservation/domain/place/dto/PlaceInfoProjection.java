package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.place.entity.Places;

import java.math.BigDecimal;

public interface PlaceInfoProjection {
    Long getPlaceId();
    String getPlaceName();
    String getDescription();
    Places.Status getStatus();   // Enum 그대로 받음
    Boolean getIsPublic();
    BigDecimal getAvgRating();
    Integer getReviewCount();
    BigDecimal getMinPrice();
    String getSido();
    String getSigungu();
    String getRoadName();
    String getDetailAddress();
    String getFileUrl();
}
