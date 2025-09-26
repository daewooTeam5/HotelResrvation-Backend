package daewoo.team5.hotelreservation.domain.place.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDTO {  // 조회 전용 DTO

    private String hotelName;

    private String description;

    private String checkIn;

    private String checkOut;

    private int capacityRoom;

    private boolean isPublic;

    private Long categoryId;

    private List<AddressDTO> addressList; // 리스트로 반환

    private List<String> images;          // 이미지 URL 리스트

    private List<RoomDTO> rooms;          // 방 정보 리스트

}