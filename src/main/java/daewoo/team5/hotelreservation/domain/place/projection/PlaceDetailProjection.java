package daewoo.team5.hotelreservation.domain.place.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PlaceDetailProjection {
    Long getId();
    String getName();
    String getDescription();
    Double getAvgRating();
    LocalDateTime getCheckIn();
    LocalDateTime getCheckOut();
    String getSido();
    String getSigungu();
    String getRoadName();
    String getDetailAddress();

    List<String> getFileUrls();

    List<RoomInfo> getRooms();
}