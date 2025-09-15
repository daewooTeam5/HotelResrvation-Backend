package daewoo.team5.hotelreservation.domain.place.projection;

import java.util.List;

public interface PlaceDetailProjection {
    Long getId();
    String getName();
    String getDescription();
    Double getAvgRating();

    String getSido();
    String getSigungu();
    String getRoadName();
    String getDetailAddress();

    // 여러 이미지
    List<String> getFileUrls();

    // 객실 상세
    List<RoomInfo> getRooms();
}