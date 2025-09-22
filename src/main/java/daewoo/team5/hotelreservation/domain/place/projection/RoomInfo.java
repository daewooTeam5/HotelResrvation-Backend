package daewoo.team5.hotelreservation.domain.place.projection;

import java.util.List;

public interface RoomInfo {
    Long getPlaceId();
    Long getRoomId();
    String getRoomType();
    String getBedType();
    Integer getCapacityPeople();
    Integer getCapacityRoom();
    Double getPrice();
    String getStatus();
    Integer getAvailableRoom();
    List<String> getImages();
}
