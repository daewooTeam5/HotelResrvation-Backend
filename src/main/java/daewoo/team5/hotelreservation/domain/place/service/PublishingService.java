package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.entity.*;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceAddressRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceCategoryRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PublishingService {//리콰이어드가 있으면 AUTOWIRED가 없어도 됨

    private final PlaceCategoryRepository  placeCategoryRepository;
    private final PlaceRepository repository;
    private final RoomRepository roomRepository;
    private final PlaceAddressRepository placeAddressRepository;

    // 등록
    public Places registerHotel(PublishingDTO dto) {
        // 1. DTO -> Places Entity 변환
        PlaceCategory placeCategory = placeCategoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));
        int capacityRoom = dto.getCapacityRoom() != null ? dto.getCapacityRoom() : 1; // 1 또는 기본값


        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .checkOut(LocalTime.parse(dto.getCheckOut()))
                .checkIn(LocalTime.parse(dto.getCheckIn()))
                .category(placeCategory)
                .isPublic(dto.isPublic())
                .capacityRoom(capacityRoom)
                .build();

// 먼저 place 저장
        Places save = repository.save(place);
/*        for(){

        }
        File.builder()
                .filename(i)
                .domainFileId(save.getId())
                .filetype("place")
                .build();*/
        // domain file id save.getId();
        // file_type = place

        // 2. DTO에 포함된 Room 정보들을 Room Entity로 변환
        // (실제로는 Room 엔티티와 빌더가 미리 정의되어 있어야 합니다)
        List<Room> rooms = dto.getRooms().stream()
                .map(roomDto -> Room.builder()
                        .roomNumber(roomDto.getRoomNumber())
                        .roomType(roomDto.getRoomType() != null && !roomDto.getRoomType().isEmpty()
                                ? roomDto.getRoomType()
                                : "single")
                        .bedType(roomDto.getBedType())
                        .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                        .capacityPeople(roomDto.getCapacityPeople())
                        .status(Room.Status.AVAILABLE)
                        .capacityRoom(roomDto.getCapacityRoom() != null ? roomDto.getCapacityRoom() : 1) // null 대비
                        .place(place)
                        .build()
                ).collect(Collectors.toList());

        roomRepository.saveAll(rooms);

        dto.getAddressList().forEach(addressDto -> {
            PlaceAddress address = PlaceAddress.builder()
                    .place(place)
                    .sido(addressDto.getSido())
                    .sigungu(addressDto.getSigungu())
                    .town(addressDto.getTown())
                    .roadName(addressDto.getRoadName())
                    .postalCode(addressDto.getPostalCode())
                    .detailAddress(addressDto.getDetailAddress())
                    .lat(BigDecimal.valueOf(221))   //하드 코딩
                    .lng(BigDecimal.valueOf(213))
                    .build();
            placeAddressRepository.save(address);
        });


        return place;
    }

    // 전체 조회
    public List<PublishingDTO> getAllHotels() {
        return repository.findAll().stream()
                .map(p -> PublishingDTO.builder()
                        .hotelName(p.getName())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    // 숙소 하나 조회
    public PublishingDTO getHotel(Long id) {
    Places place  = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("해당 숙소 없음"));
        return new PublishingDTO(//dto 모든 내용


        );
    }
}
