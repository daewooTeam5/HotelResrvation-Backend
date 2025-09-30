package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.file.service.FileService;
import daewoo.team5.hotelreservation.domain.place.dto.AddressDTO;
import daewoo.team5.hotelreservation.domain.place.dto.FileDTO;
import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomDTO;
import daewoo.team5.hotelreservation.domain.place.entity.*;
import daewoo.team5.hotelreservation.domain.place.repository.*;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublishingService {

    private final PlaceCategoryRepository placeCategoryRepository;
    private final PlaceRepository repository;
    private final RoomRepository roomRepository;
    private final PlaceAddressRepository placeAddressRepository;
    private final FileRepository fileRepository;
    private final AmenityRepository amenityRepository;
    private final PlaceAmenityRepository placeAmenityRepository;
    private final RoomAmenityRepository roomAmenityRepository;
    private final FileService fileService;
    private final UsersRepository usersRepository;

    /**
     * 숙소 등록
     */
    @Transactional
    public Places registerHotel(UserProjection user, PublishingDTO dto,
                                List<MultipartFile> hotelImages,
                                Map<Integer, List<MultipartFile>> roomImagesMap) {

        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));

        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .checkOut(LocalTime.parse(dto.getCheckOut()))
                .checkIn(LocalTime.parse(dto.getCheckIn()))
                .status(Places.Status.PENDING)
                .avgRating(BigDecimal.ZERO)
                .isPublic(true)
                .owner(usersRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new))
                .category(placeCategory)
                .build();
        repository.save(place);

        // 호텔 편의시설 저장
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(dto.getAmenityIds());
            List<PlaceAmenity> placeAmenities = amenities.stream()
                    .map(a -> PlaceAmenity.builder().place(place).amenity(a).build())
                    .toList();
            placeAmenityRepository.saveAll(placeAmenities);
        }

        // 객실 생성 + 편의시설 저장
        List<RoomDTO> roomDtos = Optional.ofNullable(dto.getRooms()).orElse(Collections.emptyList());
        for (int i = 0; i < roomDtos.size(); i++) {
            RoomDTO roomDto = roomDtos.get(i);

            Room room = Room.builder()
                    .roomType(roomDto.getRoomType() != null ? roomDto.getRoomType() : "single")
                    .bedType(roomDto.getBedType())
                    .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                    .capacityPeople(roomDto.getCapacityPeople())
                    .status(Room.Status.AVAILABLE)
                    .place(place)
                    .build();
            roomRepository.save(room);

            // ✅ 객실 편의시설 저장
            if (roomDto.getAmenityIds() != null && !roomDto.getAmenityIds().isEmpty()) {
                List<Amenity> roomAmenities = amenityRepository.findAllById(roomDto.getAmenityIds());
                List<RoomAmenityEntity> entities = roomAmenities.stream()
                        .map(a -> RoomAmenityEntity.builder()
                                .room(room)
                                .amenity(a)
                                .build())
                        .toList();
                roomAmenityRepository.saveAll(entities);
            }

            // ✅ 객실 이미지 저장
            if (roomImagesMap != null && roomImagesMap.containsKey(i)) {
                for (MultipartFile rf : roomImagesMap.get(i)) {
                    if (rf != null && !rf.isEmpty()) {
                        fileService.uploadAndSave(rf, dto.getUserId(), room.getId(), "room", null);
                    }
                }
            }
        }

        // 주소 저장
        if (dto.getAddressList() != null) {
            List<PlaceAddress> addresses = dto.getAddressList().stream()
                    .map(a -> PlaceAddress.builder()
                            .place(place)
                            .sido(a.getSido())
                            .sigungu(a.getSigungu())
                            .town(a.getTown())
                            .roadName(a.getRoadName())
                            .postalCode(a.getPostalCode())
                            .detailAddress(a.getDetailAddress())
                            .lat(BigDecimal.valueOf(221)) // TODO: 실제 좌표 반영
                            .lng(BigDecimal.valueOf(213))
                            .build())
                    .toList();
            placeAddressRepository.saveAll(addresses);
        }

        // 호텔 이미지
        if (hotelImages != null) {
            for (MultipartFile f : hotelImages) {
                if (f != null && !f.isEmpty()) {
                    fileService.uploadAndSave(f, dto.getUserId(), place.getId(), "place", null);
                }
            }
        }

        return place;
    }

    /**
     * 숙소 수정 (컨트롤러에서 호출하는 시그니처)
     */
    @Transactional
    public Places updateHotel(Long placeId, PublishingDTO dto,
                              List<MultipartFile> hotelImages,
                              Map<Integer, List<MultipartFile>> roomImagesMap) {

        Places place = repository.findById(placeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "수정할 숙소 없음", "ID=" + placeId));

        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));

        place.updateDetails(dto.getHotelName(), dto.getDescription(),
                LocalTime.parse(dto.getCheckIn()), LocalTime.parse(dto.getCheckOut()), placeCategory);

        // 호텔 편의시설 갱신
        placeAmenityRepository.deleteByPlaceId(placeId);
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(dto.getAmenityIds());
            placeAmenityRepository.saveAll(
                    amenities.stream().map(a -> PlaceAmenity.builder().place(place).amenity(a).build()).toList()
            );
        }

        // 기존 Room/이미지/주소 삭제
        List<Long> existingRoomIds = roomRepository.findByPlaceId(placeId).stream().map(Room::getId).toList();
        if (!existingRoomIds.isEmpty()) {
            roomAmenityRepository.deleteByRoomIdIn(existingRoomIds);
            fileRepository.deleteByDomainAndDomainFileIdIn("room", existingRoomIds);
        }
        fileRepository.deleteByDomainAndDomainFileId("place", placeId);
        roomRepository.deleteByPlaceId(placeId);
        placeAddressRepository.deleteByPlaceId(placeId);

        // 새로운 Room 저장
        List<RoomDTO> roomDtos = Optional.ofNullable(dto.getRooms()).orElse(Collections.emptyList());
        List<Room> savedRooms = roomRepository.saveAll(
                roomDtos.stream()
                        .map(r -> Room.builder()
                                .roomType(r.getRoomType())
                                .bedType(r.getBedType())
                                .price(BigDecimal.valueOf(r.getMinPrice()))
                                .capacityPeople(r.getCapacityPeople())
                                .status(Room.Status.AVAILABLE)
                                .place(place)
                                .build())
                        .toList()
        );

        // 객실 편의시설 저장
        for (int i = 0; i < roomDtos.size(); i++) {
            RoomDTO roomDto = roomDtos.get(i);
            Room savedRoom = savedRooms.get(i);
            if (roomDto.getAmenityIds() != null && !roomDto.getAmenityIds().isEmpty()) {
                List<Amenity> roomAmenities = amenityRepository.findAllById(roomDto.getAmenityIds());
                roomAmenityRepository.saveAll(
                        roomAmenities.stream().map(a -> RoomAmenityEntity.builder().room(savedRoom).amenity(a).build()).toList()
                );
            }
        }

        // 주소 저장
        if (dto.getAddressList() != null) {
            List<PlaceAddress> addresses = dto.getAddressList().stream()
                    .map(a -> PlaceAddress.builder()
                            .place(place)
                            .sido(a.getSido())
                            .sigungu(a.getSigungu())
                            .town(a.getTown())
                            .roadName(a.getRoadName())
                            .postalCode(a.getPostalCode())
                            .detailAddress(a.getDetailAddress())
                            .lat(BigDecimal.valueOf(221))
                            .lng(BigDecimal.valueOf(213))
                            .build())
                    .toList();
            placeAddressRepository.saveAll(addresses);
        }

        // 호텔 이미지
        if (hotelImages != null) {
            for (MultipartFile f : hotelImages) {
                if (f != null && !f.isEmpty()) {
                    fileService.uploadAndSave(f, dto.getUserId(), place.getId(), "place", null);
                }
            }
        }

        // 객실 이미지
        if (roomImagesMap != null && !roomDtos.isEmpty()) {
            for (int i = 0; i < roomDtos.size(); i++) {
                List<MultipartFile> list = roomImagesMap.get(i);
                if (list == null || list.isEmpty()) continue;
                Long roomId = savedRooms.get(i).getId();
                for (MultipartFile rf : list) {
                    if (rf != null && !rf.isEmpty()) {
                        fileService.uploadAndSave(rf, dto.getUserId(), roomId, "room", null);
                    }
                }
            }
        }

        return place;
    }

    /**
     * 숙소 단건 조회
     */
    public PublishingDTO getHotel(Long id) {
        Places place = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 숙소 없음 id=" + id));

        List<PlaceAddress> addresses = placeAddressRepository.findByPlaceId(id);
        List<Room> rooms = roomRepository.findByPlaceId(id);

        List<Long> amenityIds = placeAmenityRepository.findByPlaceId(id).stream()
                .map(pa -> pa.getAmenity().getId())
                .toList();

        return PublishingDTO.builder()
                .id(place.getId())
                .hotelName(place.getName())
                .description(place.getDescription())
                .checkIn(place.getCheckIn().toString())
                .checkOut(place.getCheckOut().toString())
                .CategoryId(place.getCategory().getId())
                .addressList(addresses.stream().map(a -> AddressDTO.builder()
                        .sido(a.getSido())
                        .sigungu(a.getSigungu())
                        .town(a.getTown())
                        .roadName(a.getRoadName())
                        .postalCode(a.getPostalCode())
                        .detailAddress(a.getDetailAddress())
                        .build()).toList())
                .rooms(rooms.stream().map(r -> RoomDTO.builder()
                        .roomType(r.getRoomType())
                        .capacityPeople(r.getCapacityPeople())
                        .minPrice(r.getPrice().intValue())
                        .bedType(r.getBedType())
                        .build()).toList())
                .amenityIds(amenityIds)
                .build();
    }

    /**
     * 숙소 전체 조회 (Owner 기준)
     */
    public List<PublishingDTO> getAllHotels(Long ownerId) {
        return repository.findAllByOwnerId(ownerId).stream()
                .map(p -> {
                    String imageUrl = fileRepository.findFirstByDomainAndDomainFileId("place", p.getId())
                            .map(File::getUrl).orElse(null);

                    AddressDTO addressDto = placeAddressRepository.findFirstByPlaceId(p.getId())
                            .map(addr -> AddressDTO.builder()
                                    .sido(addr.getSido())
                                    .sigungu(addr.getSigungu())
                                    .town(addr.getTown())
                                    .build())
                            .orElse(null);

                    return PublishingDTO.builder()
                            .id(p.getId())
                            .hotelName(p.getName())
                            .description(p.getDescription())
                            .minPrice(p.getMinPrice())
                            .checkIn(p.getCheckIn().toString())
                            .checkOut(p.getCheckOut().toString())
                            .address(addressDto)
                            .images(imageUrl != null ? List.of(imageUrl) : List.of())
                            .CategoryId((long) p.getCategory().getId())
                            .build();
                })
                .toList();
    }

    /**
     * 숙소 삭제
     */
    @Transactional
    public void deleteHotel(Long placeId) {
        if (!repository.existsById(placeId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "삭제할 숙소 없음", "ID=" + placeId);
        }
        List<Long> roomIds = roomRepository.findByPlaceId(placeId).stream().map(Room::getId).toList();
        if (!roomIds.isEmpty()) {
            roomAmenityRepository.deleteByRoomIdIn(roomIds);
            fileRepository.deleteByDomainAndDomainFileIdIn("room", roomIds);
            roomRepository.deleteByPlaceId(placeId);
        }
        fileRepository.deleteByDomainAndDomainFileId("place", placeId);
        placeAddressRepository.deleteByPlaceId(placeId);
        placeAmenityRepository.deleteByPlaceId(placeId);
        repository.deleteById(placeId);
    }

    private String extractExtensionFromDataUrl(String dataUrl) {
        if (dataUrl == null || !dataUrl.startsWith("data:image/")) {
            return "jpg";
        }
        Pattern pattern = Pattern.compile("data:image/(\\w+);base64,.*");
        Matcher matcher = pattern.matcher(dataUrl);
        if (matcher.find()) return matcher.group(1);
        return "jpg";
    }
}
