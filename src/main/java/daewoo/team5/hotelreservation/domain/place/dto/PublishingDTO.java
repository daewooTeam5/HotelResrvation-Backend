package daewoo.team5.hotelreservation.domain.place.dto;


import daewoo.team5.hotelreservation.domain.place.entity.ImageList;

import daewoo.team5.hotelreservation.domain.place.entity.PlaceAddress;
import daewoo.team5.hotelreservation.domain.place.entity.PlaceService;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishingDTO {

    private String hotelName;

    private String hotelType;

    private String description;

    private String checkIn;

    private String checkOut;

    private List<AddressDTO> addressList;

    private List<String> images; // Base64 인코딩된 문자열 리스트

    private List<String> amenities; // 편의시설 이름 리스트

    private List<DiscountDTO> discounts;

    private List<RoomDTO> rooms;

    private int CategoryId;

    private Integer capacityRoom;

    private boolean isPublic;

}
