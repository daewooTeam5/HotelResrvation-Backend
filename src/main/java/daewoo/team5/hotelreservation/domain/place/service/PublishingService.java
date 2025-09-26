package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.AddressDTO;
import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomDTO;
import daewoo.team5.hotelreservation.domain.place.dto.SearchDTO;
import daewoo.team5.hotelreservation.domain.place.entity.*;
import daewoo.team5.hotelreservation.domain.place.repository.*;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
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

    private final PlaceCategoryRepository placeCategoryRepository;
    private final PlaceRepository repository;
    private final RoomRepository roomRepository;
    private final PlaceAddressRepository placeAddressRepository;
    private final ImageListRepository imageListRepository;
    private final UsersRepository usersRepository;

    // 등록
    public Places registerHotel(PublishingDTO dto, UserProjection user) {
        // 1. 카테고리 조회
        PlaceCategory placeCategory = placeCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));
        // 유저 조회
        Users users = usersRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        // 2. Places 엔티티 생성 및 저장
        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .checkOut(LocalTime.parse(dto.getCheckOut()))
                .checkIn(LocalTime.parse(dto.getCheckIn()))
                .category(placeCategory)
                .isPublic(dto.isPublic())
                .owner(users)
                .capacityRoom(dto.getCapacityRoom() != null ? dto.getCapacityRoom() : 1)
                .build();

        // save 후의 엔티티를 사용하기 위해 'place' 변수를 갱신
        place = repository.save(place);

        // 3. Room 엔티티 리스트 변환 및 저장
        if (dto.getRooms() != null && !dto.getRooms().isEmpty()) {
            final Places finalPlace = place; // 람다식 내부에서 사용하기 위해 final 또는 effectively final 변수 사용
            List<Room> rooms = dto.getRooms().stream()
                    .map(roomDto -> Room.builder()
                            .roomNumber(roomDto.getRoomNumber())
                            .roomType(roomDto.getRoomType() != null && !roomDto.getRoomType().isEmpty() ? roomDto.getRoomType() : "single")
                            .bedType(roomDto.getBedType())
                            .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                            .capacityPeople(roomDto.getCapacityPeople())
                            .status(Room.Status.AVAILABLE)
                            .capacityRoom(roomDto.getCapacityRoom() != null ? roomDto.getCapacityRoom() : 1)
                            .place(finalPlace)
                            .build()
                    ).collect(Collectors.toList());
            roomRepository.saveAll(rooms);
        }

        // 4. ImageList 엔티티 리스트 변환 및 저장 (수정된 로직)
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            final Places finalPlace = place;
            List<ImageList> imageLists = dto.getImages().stream()
                    .map(imageUrl -> ImageList.builder()
                            .imageUrl(imageUrl)
                            .place(finalPlace)
                            .build()
                    ).collect(Collectors.toList());
            imageListRepository.saveAll(imageLists);
        }

        // 5. PlaceAddress 엔티티 리스트 변환 및 저장
        if (dto.getAddress() != null) {
            PlaceAddress address = PlaceAddress.builder()
                    .place(place)
                    .sido(dto.getAddress().getSido())
                    .sigungu(dto.getAddress().getSigungu())
                    .town(dto.getAddress().getTown())
                    .roadName(dto.getAddress().getRoadName())
                    .postalCode(dto.getAddress().getPostalCode())
                    .detailAddress(dto.getAddress().getDetailAddress())
                    .lat(BigDecimal.valueOf(221))
                    .lng(BigDecimal.valueOf(213))
                    .build();
            placeAddressRepository.save(address);
        }


        return place;
    }

    //내 호텔 전체 조회
    public List<SearchDTO> getMyHotels(Long ownerId) {
        return repository.findByOwnerId(ownerId)   // DB에서 바로 ownerId 필터링
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteHotel(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("해당 숙소 없음");
        }
        repository.deleteById(id);
    }

    private SearchDTO toDTO(Places place) {
        // 이미지 리스트
        List<String> images = imageListRepository.findByPlaceId(place.getId())
                .stream()
                .map(ImageList::getImageUrl)
                .collect(Collectors.toList());

        // 주소 리스트 (1개만 있어도 리스트 형태로 반환)
        List<AddressDTO> addressList = placeAddressRepository.findByPlace_Id(place.getId())
                .stream()
                .map(addr -> AddressDTO.builder()
                        .sido(addr.getSido())
                        .sigungu(addr.getSigungu())
                        .town(addr.getTown())
                        .roadName(addr.getRoadName())
                        .postalCode(addr.getPostalCode())
                        .detailAddress(addr.getDetailAddress())
                        .build())
                .collect(Collectors.toList());
        // 방 리스트
        List<RoomDTO> rooms = roomRepository.findByPlace_Id(place.getId())
                .stream()
                .map(room -> RoomDTO.builder()
                        .roomNumber(room.getRoomNumber())
                        .roomType(room.getRoomType())
                        .capacityPeople(room.getCapacityPeople())
                        .capacityRoom(room.getCapacityRoom())
                        .minPrice((int) room.getPrice().doubleValue())
                        .bedType(room.getBedType())
                        .build())
                .collect(Collectors.toList());

        // DTO 빌드
        return SearchDTO.builder()
                .hotelName(place.getName())
                .description(place.getDescription())
                .checkIn(place.getCheckIn() != null ? place.getCheckIn().toString() : null)
                .checkOut(place.getCheckOut() != null ? place.getCheckOut().toString() : null)
                .images(images)
                .addressList(addressList)
                .rooms(rooms)
                .capacityRoom(place.getCapacityRoom())
                .isPublic(Boolean.TRUE.equals(place.getIsPublic()))
                .categoryId(place.getCategory() != null ? place.getCategory().getId() : null)
                .build();
    }



}

