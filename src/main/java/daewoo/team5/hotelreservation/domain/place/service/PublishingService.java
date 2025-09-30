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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PublishingService.class);
    private final PlaceCategoryRepository placeCategoryRepository;
    private final PlaceRepository repository;
    private final RoomRepository roomRepository;
    private final PlaceAddressRepository placeAddressRepository;
    private final FileRepository fileRepository;
    private final AmentiesRepository amentiesRepository;
    private final FileService fileService;
    private final UsersRepository usersRepository;


    @Deprecated
    @Transactional
    public Places registerHotel(PublishingDTO dto) {
        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));


        List<Amenity> amenityList = new ArrayList<>();
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            amenityList = amentiesRepository.findAllById(dto.getAmenityIds());
        }
        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .checkOut(LocalTime.parse(dto.getCheckOut()))
                .checkIn(LocalTime.parse(dto.getCheckIn()))
//                .amenities(amenityList)
                .category(placeCategory)
                .build();

        repository.save(place);

        List<File> allFilesToSave = new ArrayList<>();

        if (dto.getHotelImages() != null) {
            dto.getHotelImages().forEach(imgDto -> {
                String url = imgDto.getUrl();
                String ext = (imgDto.getExtension() != null && !imgDto.getExtension().isEmpty())
                        ? imgDto.getExtension()
                        : extractExtensionFromDataUrl(url);
                allFilesToSave.add(File.builder()
                        .domain("place")
                        .domainFileId(place.getId())
                        .filename(UUID.randomUUID().toString())
                        .extension(ext)
                        .filetype("image")

                        .url(url)
                        .userId(dto.getUserId())
                        .build());
            });
        }

        List<Room> rooms = dto.getRooms().stream()
                .map(roomDto -> Room.builder()
                        .roomType(roomDto.getRoomType() != null && !roomDto.getRoomType().isEmpty()
                                ? roomDto.getRoomType()
                                : "single")
                        .roomType(roomDto.getRoomType())
                        .bedType(roomDto.getBedType())
                        .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                        .capacityPeople(roomDto.getCapacityPeople())
                        .status(Room.Status.AVAILABLE)
                        .place(place)
                        .build())
                .collect(Collectors.toList());

        List<Room> savedRooms = roomRepository.saveAll(rooms);

        for (int i = 0; i < dto.getRooms().size(); i++) {
            RoomDTO roomDto = dto.getRooms().get(i);
            Room savedRoom = savedRooms.get(i);
            if (roomDto.getImages() != null) {
                roomDto.getImages().forEach(imgDto -> {
                    String url = imgDto.getUrl();
                    String ext = (imgDto.getExtension() != null && !imgDto.getExtension().isEmpty())
                            ? imgDto.getExtension()
                            : extractExtensionFromDataUrl(url);
                    allFilesToSave.add(File.builder()
                            .domain("room")
                            .domainFileId(savedRoom.getId())
                            .filename(UUID.randomUUID().toString())
                            .extension(ext)
                            .filetype("image")
                            .url(url)
                            .userId(dto.getUserId())
                            .build());
                });
            }
        }

        if (!allFilesToSave.isEmpty()) {
            fileRepository.saveAll(allFilesToSave);
        }

        List<PlaceAddress> addresses = dto.getAddressList().stream()
                .map(addressDto -> PlaceAddress.builder()
                        .place(place)
                        .sido(addressDto.getSido())
                        .sigungu(addressDto.getSigungu())
                        .town(addressDto.getTown())
                        .roadName(addressDto.getRoadName())
                        .postalCode(addressDto.getPostalCode())
                        .detailAddress(addressDto.getDetailAddress())
                        .lat(BigDecimal.valueOf(221))
                        .lng(BigDecimal.valueOf(213))
                        .build())
                .collect(Collectors.toList());

        placeAddressRepository.saveAll(addresses);

        return place;
    }

    // 멀티파트 입력을 받아 업로드까지 처리하는 등록 메서드
    @Transactional
    public Places registerHotel(UserProjection user, PublishingDTO dto, List<MultipartFile> hotelImages,
                                Map<Integer, List<MultipartFile>> roomImagesMap) {
        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));

        List<Amenity> allById = amentiesRepository.findAllById(dto.getAmenityIds());
        log.info("=12==================");
        log.info(dto.getRooms().toString());
        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .checkOut(LocalTime.parse(dto.getCheckOut()))
                .checkIn(LocalTime.parse(dto.getCheckIn()))
                .status(Places.Status.PENDING) // 기본값 설정
                .avgRating(BigDecimal.ZERO)
                .isPublic(true)
                .owner(usersRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new))
                .category(placeCategory)
                .build();
        repository.save(place);

        // 객실 생성 (null-safe)
        List<RoomDTO> roomDtos = Optional.ofNullable(dto.getRooms()).orElse(Collections.emptyList());
        System.out.println("roomdto1-12312");
        System.out.println(roomDtos);
        List<Room> rooms = roomDtos.stream()
                .map(roomDto -> Room.builder()
                        .roomType(roomDto.getRoomType() != null && !roomDto.getRoomType().isEmpty()
                                ? roomDto.getRoomType()
                                : "single")
                        .bedType(roomDto.getBedType())
                        .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                        .capacityPeople(roomDto.getCapacityPeople())
                        .status(Room.Status.AVAILABLE)
                        .place(place)
                        .build())
                .collect(Collectors.toList());
        List<Room> savedRooms = roomRepository.saveAll(rooms);

        // 주소 저장
        if (dto.getAddressList() != null) {
            List<PlaceAddress> addresses = dto.getAddressList().stream()
                    .map(addressDto -> PlaceAddress.builder()
                            .place(place)
                            .sido(addressDto.getSido())
                            .sigungu(addressDto.getSigungu())
                            .town(addressDto.getTown())
                            .roadName(addressDto.getRoadName())
                            .postalCode(addressDto.getPostalCode())
                            .detailAddress(addressDto.getDetailAddress())
                            .lat(BigDecimal.valueOf(221))
                            .lng(BigDecimal.valueOf(213))
                            .build())
                    .collect(Collectors.toList());
            placeAddressRepository.saveAll(addresses);
        }

        // 호텔 이미지 업로드/저장
        if (hotelImages != null) {
            for (MultipartFile f : hotelImages) {
                if (f == null || f.isEmpty()) continue;
                fileService.uploadAndSave(f, dto.getUserId(), place.getId(), "place", null);
            }
        }

        // 객실 이미지 업로드/저장 (null-safe)
        if (roomImagesMap != null && !roomDtos.isEmpty()) {
            for (int i = 0; i < roomDtos.size(); i++) {
                List<MultipartFile> list = roomImagesMap.get(i);
                if (list == null || list.isEmpty()) continue;
                Long roomId = savedRooms.get(i).getId();
                for (MultipartFile rf : list) {
                    if (rf == null || rf.isEmpty()) continue;
                    fileService.uploadAndSave(rf, dto.getUserId(), roomId, "room", null);
                }
            }
        }
        return place;
    }

    // PublishingService.java

    @Transactional
    public Places updateHotel(Long placeId, PublishingDTO dto) {
        // 1. 수정할 숙소 엔티티를 찾습니다.
        Places place = repository.findById(placeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "수정할 숙소를 찾을 수 없습니다.", "ID: " + placeId));

        // 2. 카테고리 엔티티를 찾습니다.
        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));

        // 3. 호텔의 기본 정보를 업데이트합니다.
        place.updateDetails(
                dto.getHotelName(),
                dto.getDescription(),
                LocalTime.parse(dto.getCheckIn()),
                LocalTime.parse(dto.getCheckOut()),
                placeCategory
        );

        // 4. 편의시설(Amenity) 목록을 업데이트합니다.
        if (dto.getAmenityIds() != null) {
            List<Amenity> updatedAmenities = amentiesRepository.findAllById(dto.getAmenityIds());
//            place.setAmenities(updatedAmenities);
        } else {
//            place.getAmenities().clear();
        }

        // 5. 기존의 연관 데이터(이미지, 객실, 주소)를 모두 삭제합니다.
        List<Long> existingRoomIds = roomRepository.findByPlaceId(placeId).stream()
                .map(Room::getId).collect(Collectors.toList());
        if (!existingRoomIds.isEmpty()) {
            fileRepository.deleteByDomainAndDomainFileIdIn("room", existingRoomIds);
        }
        fileRepository.deleteByDomainAndDomainFileId("place", placeId);
        roomRepository.deleteByPlaceId(placeId);
        placeAddressRepository.deleteByPlaceId(placeId);

        // 💡 [추가] 6. DTO에 담겨온 새 정보로 연관 데이터를 다시 생성합니다.
        // 이 부분은 registerHotel의 로직과 동일합니다.
        List<File> allFilesToSave = new ArrayList<>();

        if (dto.getHotelImages() != null) {
            dto.getHotelImages().forEach(imgDto -> {
                String url = imgDto.getUrl();
                String ext = (imgDto.getExtension() != null && !imgDto.getExtension().isEmpty())
                        ? imgDto.getExtension()
                        : extractExtensionFromDataUrl(url);
                allFilesToSave.add(File.builder()
                        .domain("place")
                        .domainFileId(place.getId())
                        .filename(UUID.randomUUID().toString())
                        .extension(ext)
                        .filetype("image")
                        .url(url)
                        .userId(dto.getUserId())
                        .build());
            });
        }

        List<Room> rooms = dto.getRooms().stream()
                .map(roomDto -> Room.builder()
                        .roomType(roomDto.getRoomType())
                        .bedType(roomDto.getBedType())
                        .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                        .capacityPeople(roomDto.getCapacityPeople())
                        .status(Room.Status.AVAILABLE)
                        .place(place)
                        .build())
                .collect(Collectors.toList());

        List<Room> savedRooms = roomRepository.saveAll(rooms);

        for (int i = 0; i < dto.getRooms().size(); i++) {
            RoomDTO roomDto = dto.getRooms().get(i);
            Room savedRoom = savedRooms.get(i);
            if (roomDto.getImages() != null) {
                roomDto.getImages().forEach(imgDto -> {
                    String url = imgDto.getUrl();
                    String ext = (imgDto.getExtension() != null && !imgDto.getExtension().isEmpty())
                            ? imgDto.getExtension()
                            : extractExtensionFromDataUrl(url);
                    allFilesToSave.add(File.builder()
                            .domain("room")
                            .domainFileId(savedRoom.getId())
                            .filename(UUID.randomUUID().toString())
                            .extension(ext)
                            .filetype("image")
                            .url(url)
                            .userId(dto.getUserId())
                            .build());
                });
            }
        }

        if (!allFilesToSave.isEmpty()) {
            fileRepository.saveAll(allFilesToSave);
        }

        List<PlaceAddress> addresses = dto.getAddressList().stream()
                .map(addressDto -> PlaceAddress.builder()
                        .place(place)
                        .sido(addressDto.getSido())
                        .sigungu(addressDto.getSigungu())
                        .town(addressDto.getTown())
                        .roadName(addressDto.getRoadName())
                        .postalCode(addressDto.getPostalCode())
                        .detailAddress(addressDto.getDetailAddress())
                        .lat(BigDecimal.valueOf(221))
                        .lng(BigDecimal.valueOf(213))
                        .build())
                .collect(Collectors.toList());

        placeAddressRepository.saveAll(addresses);

        return place;
    }

    // 멀티파트 입력을 받아 업로드까지 처리하는 수정 메서드
    @Transactional
    public Places updateHotel(Long placeId, PublishingDTO dto,
                              List<MultipartFile> hotelImages,
                              Map<Integer, List<MultipartFile>> roomImagesMap) {
        Places place = repository.findById(placeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "수정할 숙소를 찾을 수 없습니다.", "ID: " + placeId));

        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));

        place.updateDetails(
                dto.getHotelName(),
                dto.getDescription(),
                LocalTime.parse(dto.getCheckIn()),
                LocalTime.parse(dto.getCheckOut()),
                placeCategory
        );

        if (dto.getAmenityIds() != null) {
            List<Amenity> updatedAmenities = amentiesRepository.findAllById(dto.getAmenityIds());
//            place.setAmenities(updatedAmenities);
        } else {
//            place.getAmenities().clear();
        }

        List<Long> existingRoomIds = roomRepository.findByPlaceId(placeId).stream()
                .map(Room::getId).collect(Collectors.toList());
        if (!existingRoomIds.isEmpty()) {
            fileRepository.deleteByDomainAndDomainFileIdIn("room", existingRoomIds);
        }
        fileRepository.deleteByDomainAndDomainFileId("place", placeId);
        roomRepository.deleteByPlaceId(placeId);
        placeAddressRepository.deleteByPlaceId(placeId);

        // 신규 객실 생성 (null-safe)
        List<RoomDTO> roomDtos = Optional.ofNullable(dto.getRooms()).orElse(Collections.emptyList());
        List<Room> rooms = roomDtos.stream()
                .map(roomDto -> Room.builder()
                        .roomType(roomDto.getRoomType())
                        .bedType(roomDto.getBedType())
                        .price(BigDecimal.valueOf(roomDto.getMinPrice()))
                        .capacityPeople(roomDto.getCapacityPeople())
                        .status(Room.Status.AVAILABLE)
                        .place(place)
                        .build())
                .collect(Collectors.toList());
        List<Room> savedRooms = roomRepository.saveAll(rooms);

        if (dto.getAddressList() != null) {
            List<PlaceAddress> addresses = dto.getAddressList().stream()
                    .map(addressDto -> PlaceAddress.builder()
                            .place(place)
                            .sido(addressDto.getSido())
                            .sigungu(addressDto.getSigungu())
                            .town(addressDto.getTown())
                            .roadName(addressDto.getRoadName())
                            .postalCode(addressDto.getPostalCode())
                            .detailAddress(addressDto.getDetailAddress())
                            .lat(BigDecimal.valueOf(221))
                            .lng(BigDecimal.valueOf(213))
                            .build())
                    .collect(Collectors.toList());
            placeAddressRepository.saveAll(addresses);
        }

        if (hotelImages != null) {
            for (MultipartFile f : hotelImages) {
                if (f == null || f.isEmpty()) continue;
                fileService.uploadAndSave(f, dto.getUserId(), place.getId(), "place", null);
            }
        }
        if (roomImagesMap != null && !roomDtos.isEmpty()) {
            for (int i = 0; i < roomDtos.size(); i++) {
                List<MultipartFile> list = roomImagesMap.get(i);
                if (list == null || list.isEmpty()) continue;
                Long roomId = savedRooms.get(i).getId();
                for (MultipartFile rf : list) {
                    if (rf == null || rf.isEmpty()) continue;
                    fileService.uploadAndSave(rf, dto.getUserId(), roomId, "room", null);
                }
            }
        }
        return place;
    }

    private String extractExtensionFromDataUrl(String dataUrl) {
        if (dataUrl == null || !dataUrl.startsWith("data:image/")) {
            return "jpg";
        }
        Pattern pattern = Pattern.compile("data:image/(\\w+);base64,.*");
        Matcher matcher = pattern.matcher(dataUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "jpg";
    }

    public List<PublishingDTO> getAllHotels(Long ownerId) {
        return repository.findAllByOwnerId(ownerId).stream()
                .map(p -> {
                    String imageUrl = fileRepository.findFirstByDomainAndDomainFileId("place", p.getId())
                            .map(File::getUrl)
                            .orElse(null);
                    AddressDTO addressDto = placeAddressRepository.findFirstByPlaceId(p.getId())
                            .map(addr -> AddressDTO.builder()
                                    .sido(addr.getSido())
                                    .sigungu(addr.getSigungu())
                                    .town(addr.getTown())
                                    .build())
                            .orElse(null);

                    Long categoryId = p.getCategory() != null ? (long)p.getCategory().getId() : null;


                    return PublishingDTO.builder()
                            .id(p.getId())
                            .hotelName(p.getName())
                            .description(p.getDescription())
                            .minPrice(p.getMinPrice())
                            .checkIn(p.getCheckIn().toString())
                            .checkOut(p.getCheckOut().toString())
                            .address(addressDto)
                            .images(imageUrl != null ? List.of(imageUrl) : List.of())
                            .CategoryId(categoryId)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public PublishingDTO getHotel(Long id) {
        Places place = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 숙소를 찾을 수 없습니다. id=" + id));

        List<PlaceAddress> addresses = placeAddressRepository.findByPlaceId(id);
        List<Room> rooms = roomRepository.findByPlaceId(id);

        List<File> hotelImages = fileRepository.findByDomainAndDomainFileId("place", id);
        List<Long> roomIds = rooms.stream().map(Room::getId).collect(Collectors.toList());
        List<File> roomImages = fileRepository.findByDomainAndDomainFileIdIn("room", roomIds);

        List<AddressDTO> addressDTOs = addresses.stream()
                .map(addr -> AddressDTO.builder()
                        .sido(addr.getSido())
                        .sigungu(addr.getSigungu())
                        .town(addr.getTown())
                        .roadName(addr.getRoadName())
                        .postalCode(addr.getPostalCode())
                        .detailAddress(addr.getDetailAddress())
                        .build())
                .collect(Collectors.toList());

        List<FileDTO> hotelImageDTOs = hotelImages.stream()
                .map(file -> new FileDTO(file.getFilename(), file.getExtension(), file.getUrl()))
                .collect(Collectors.toList());

        List<RoomDTO> roomDTOs = rooms.stream().map(room -> {
            List<File> currentRoomImages = roomImages.stream()
                    .filter(img -> img.getDomainFileId().equals(room.getId()))
                    .collect(Collectors.toList());
            List<FileDTO> currentRoomImageDTOs = currentRoomImages.stream()
                    .map(file -> new FileDTO(file.getFilename(), file.getExtension(), file.getUrl()))
                    .collect(Collectors.toList());

            return RoomDTO.builder()
                    .roomType(room.getRoomType())
                    .capacityPeople(room.getCapacityPeople())
                    .minPrice(room.getPrice().intValue())
                    .bedType(room.getBedType())
                    .images(currentRoomImageDTOs)
                    .build();
        }).collect(Collectors.toList());

//        List<Long> amenityIds = place.getAmenities().stream()
//                .map(Amenity::getId)
//                .collect(Collectors.toList());

        return PublishingDTO.builder()
                .hotelName(place.getName())
                .description(place.getDescription())
                .checkIn(place.getCheckIn().toString())
                .checkOut(place.getCheckOut().toString())
                .CategoryId(place.getCategory().getId())
                .addressList(addressDTOs)
                .hotelImages(hotelImageDTOs)
                .rooms(roomDTOs)
//                .amenityIds(amenityIds)
                .build();
    }

    @Transactional
    public void deleteHotel(Long placeId) {
        // 1. 숙소(Place)가 존재하는지 확인
        if (!repository.existsById(placeId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "삭제할 숙소를 찾을 수 없습니다.", "ID: " + placeId);
        }

        List<Long> roomIds = roomRepository.findByPlaceId(placeId).stream()
                .map(Room::getId).collect(Collectors.toList());
        if (!roomIds.isEmpty()) {
            fileRepository.deleteByDomainAndDomainFileIdIn("room", roomIds);
        }
        fileRepository.deleteByDomainAndDomainFileId("place", placeId);
        roomRepository.deleteByPlaceId(placeId);
        placeAddressRepository.deleteByPlaceId(placeId);

        /*if (!roomIds.isEmpty()) {
            // 💡 [추가] Room을 삭제하기 전에, Room을 참조하는 예약(Reservation) 데이터를 먼저 삭제해야 합니다.
            // 이 라인이 누락되었습니다!
            dailyPlaceReservationRepository.deleteByRoomIdIn(roomIds);

            // 그 다음, 기존 로직대로 Room의 이미지와 Room 자체를 삭제합니다.
            fileRepository.deleteByDomainAndDomainFileIdIn("room", roomIds);
            roomRepository.deleteByPlaceId(placeId);
        }*/

        // 3. 마지막으로 숙소(Place) 자체를 삭제
        repository.deleteById(placeId);
    }
}

