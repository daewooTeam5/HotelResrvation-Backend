package daewoo.team5.hotelreservation.domain.place.projection;

public interface RoomInfoProjection {
    Long getId();
    Long getPlaceId();
    String getRoomName();
    Integer getPrice();
    Integer getDiscountPercent();
    Integer getFinalPrice();
    Integer getAvailableCount();
}

