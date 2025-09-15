package daewoo.team5.hotelreservation.domain.place.dto;

import daewoo.team5.hotelreservation.domain.place.projection.PlaceDetailProjection;
import daewoo.team5.hotelreservation.domain.place.projection.RoomInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PlaceDetailResponse {
    private String name;
    private String description;
    private Double avgRating;

    private String sido;
    private String sigungu;
    private String roadName;
    private String detailAddress;

    private List<String> fileUrls;
    private List<RoomInfo> rooms;

    public PlaceDetailResponse(PlaceDetailProjection detail,
                               List<String> fileUrls,
                               List<RoomInfo> rooms) {
        this.name = detail.getName();
        this.description = detail.getDescription();
        this.avgRating = detail.getAvgRating();
        this.sido = detail.getSido();
        this.sigungu = detail.getSigungu();
        this.roadName = detail.getRoadName();
        this.detailAddress = detail.getDetailAddress();
        this.fileUrls = fileUrls;
        this.rooms = rooms;
    }
}
