package daewoo.team5.hotelreservation.domain.place.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.dto.AddressDTO;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.dto.RoomDTO;
import daewoo.team5.hotelreservation.domain.place.service.PublishingService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel/publishing")
@RequiredArgsConstructor
public class PublishingController {

    private final PublishingService publishingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/register")
    public ResponseEntity<ApiResult<PublishingDTO>> registerHotel(@RequestBody PublishingDTO dto) {
        try {
            Places savedPlace = publishingService.registerHotel(dto);

            List<RoomDTO> roomDTOs = savedPlace.getRooms().stream()
                    .map(r -> RoomDTO.builder()
                            .roomNumber(r.getId().intValue())
                            .price(r.getPrice().intValue())
                            .CapacityPeople(r.getCapacityPeople())
                            .extraPrice(0)
                            .roomType(r.getRoomType())
                            .checkIn(null)
                            .checkOut(null)
                            .bedType(null) // 필요 시 JSON 변환
                            .build())
                    .toList();

            List<AddressDTO> addressDTOs = savedPlace.getAddresses().stream()
                    .map(a -> new AddressDTO(
                            a.getSigungu(),
                            a.getSido(),
                            a.getRoadName(),
                            a.getPostalCode(),
                            a.getDetailAddress()
                    ))
                    .toList();

            List<String> images = savedPlace.getImages().stream()
                    .map(i -> i.getImageUrl())
                    .toList();

            PublishingDTO responseDto = PublishingDTO.builder()
                    .hotelName(savedPlace.getName())
                    .addressList(addressDTOs)
                    .images(images)
                    .rooms(roomDTOs)
                    .description(savedPlace.getDescription())
                    .build();

            return ResponseEntity.ok(ApiResult.ok(responseDto));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResult.<PublishingDTO>ok(null, "숙소 등록 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResult<List<PublishingDTO>>> getAllHotels() {
        List<PublishingDTO> hotels = publishingService.getAllHotels();
        return ResponseEntity.ok(ApiResult.ok(hotels));
    }
}
