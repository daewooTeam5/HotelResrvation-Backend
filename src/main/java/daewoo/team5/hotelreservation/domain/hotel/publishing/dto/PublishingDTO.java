package daewoo.team5.hotelreservation.domain.hotel.publishing.dto;

import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Address;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Amenities;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.ImageList;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Room;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishingDTO {

    private String hotelName;   //숙소 이름

    private List<AddressDTO> addressList;    //주소 가져오기

    private List<String> images; //이미지

    private List<RoomDTO> rooms;    //숙소 여러개 넣을거라 리스트로 숙소 기본 정보 제공받기

    private List<String> amenities; //편의시설 리스트

    private String description;   //설명


    public PublishingDTO(String hotelName, List<Address> addresses, List<ImageList> images, List<Room> rooms, String introduction, List<Amenities> amenities) {
    }
}
