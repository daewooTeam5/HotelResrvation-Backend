package daewoo.team5.hotelreservation.domain.place.projection;

public interface RoomInfo {
    Long getRoomId();
    String getRoomType();
    String getBedType();
    Integer getCapacityPeople();
    Integer getCapacityRoom();
    Double getPrice();
    String getStatus();
}
