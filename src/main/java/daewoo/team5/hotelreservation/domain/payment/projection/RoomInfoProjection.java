package daewoo.team5.hotelreservation.domain.payment.projection;

public interface RoomInfoProjection {
    Long getId();
    String getRoomName();
    Integer getAvailableCount();
    Long getPlaceId();
    Integer getPrice();
    Integer getDiscountPercent();
    Integer getFinalPrice();
}
