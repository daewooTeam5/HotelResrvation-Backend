package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.AddressDTO;
import daewoo.team5.hotelreservation.domain.place.dto.FileDTO;
import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomDTO;
import daewoo.team5.hotelreservation.domain.place.entity.*;
import daewoo.team5.hotelreservation.domain.place.repository.*;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

    @Transactional
    public Places registerHotel(PublishingDTO dto) {
        PlaceCategory placeCategory = placeCategoryRepository.findById(Math.toIntExact(dto.getCategoryId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "카테고리 없음", ""));

        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .checkOut(LocalTime.parse(dto.getCheckOut()))
                .checkIn(LocalTime.parse(dto.getCheckIn()))
                .category(placeCategory)
                .build();

        repository.save(place);

        List<File> allFilesToSave = new ArrayList<>();

        if (dto.getHotelImages() != null) {
            dto.getHotelImages().forEach(imgDto -> {
                String url = imgDto.getUrl();
                allFilesToSave.add(File.builder()
                        .domain("place")
                        .domainFileId(place.getId())
                        .filename(UUID.randomUUID().toString())
                        .extension(extractExtensionFromDataUrl(url))
                        .filetype("image")
                        .url(url)
                        .userId(dto.getUserId())
                        .build());
            });
        }

        List<Room> rooms = dto.getRooms().stream()
                .map(roomDto -> Room.builder()
                        .roomNumber(roomDto.getRoomNumber())
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
                    allFilesToSave.add(File.builder()
                            .domain("room")
                            .domainFileId(savedRoom.getId())
                            .filename(UUID.randomUUID().toString())
                            .extension(extractExtensionFromDataUrl(url))
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

    @Transactional
    public Places updateHotel(Long placeId, PublishingDTO dto) {
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

        List<Long> existingRoomIds = roomRepository.findByPlaceId(placeId).stream()
                .map(Room::getId).collect(Collectors.toList());

        if (!existingRoomIds.isEmpty()) {
            fileRepository.deleteByDomainAndDomainFileIdIn("room", existingRoomIds);
        }
        fileRepository.deleteByDomainAndDomainFileId("place", placeId);
        roomRepository.deleteByPlaceId(placeId);
        placeAddressRepository.deleteByPlaceId(placeId);

        List<File> allFilesToSave = new ArrayList<>();
        if (dto.getHotelImages() != null) {
            dto.getHotelImages().forEach(imgDto -> {
                String url = imgDto.getUrl();
                allFilesToSave.add(File.builder()
                        .domain("place").domainFileId(place.getId())
                        .filename(UUID.randomUUID().toString()).extension(extractExtensionFromDataUrl(url))
                        .filetype("image").url(url).userId(dto.getUserId()).build());
            });
        }

        List<Room> rooms = dto.getRooms().stream()
                .map(roomDto -> Room.builder()
                        .roomNumber(roomDto.getRoomNumber()).roomType(roomDto.getRoomType())
                        .bedType(roomDto.getBedType()).price(BigDecimal.valueOf(roomDto.getMinPrice()))
                        .capacityPeople(roomDto.getCapacityPeople()).status(Room.Status.AVAILABLE)
                        .place(place).build())
                .collect(Collectors.toList());
        List<Room> savedRooms = roomRepository.saveAll(rooms);

        for (int i = 0; i < dto.getRooms().size(); i++) {
            RoomDTO roomDto = dto.getRooms().get(i);
            Room savedRoom = savedRooms.get(i);
            if (roomDto.getImages() != null) {
                roomDto.getImages().forEach(imgDto -> {
                    String url = imgDto.getUrl();
                    allFilesToSave.add(File.builder()
                            .domain("room").domainFileId(savedRoom.getId())
                            .filename(UUID.randomUUID().toString()).extension(extractExtensionFromDataUrl(url))
                            .filetype("image").url(url).userId(dto.getUserId()).build());
                });
            }
        }

        if (!allFilesToSave.isEmpty()) {
            fileRepository.saveAll(allFilesToSave);
        }

        List<PlaceAddress> addresses = dto.getAddressList().stream()
                .map(addressDto -> PlaceAddress.builder()
                        .place(place).sido(addressDto.getSido()).sigungu(addressDto.getSigungu())
                        .town(addressDto.getTown()).roadName(addressDto.getRoadName())
                        .postalCode(addressDto.getPostalCode()).detailAddress(addressDto.getDetailAddress())
                        .lat(BigDecimal.valueOf(221)).lng(BigDecimal.valueOf(213)).build())
                .collect(Collectors.toList());
        placeAddressRepository.saveAll(addresses);

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

                    return PublishingDTO.builder()
                            .id(p.getId())
                            .hotelName(p.getName())
                            .description(p.getDescription())
                            .minPrice(p.getMinPrice())
                            .checkIn(p.getCheckIn().toString())
                            .checkOut(p.getCheckOut().toString())
                            .address(addressDto)
                            .images(imageUrl != null ? List.of(imageUrl) : List.of())
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
            List<FileDTO> currentRoomImages = roomImages.stream()
                    .filter(img -> img.getDomainFileId().equals(room.getId()))
                    .map(file -> new FileDTO(file.getFilename(), file.getExtension(), file.getUrl()))
                    .collect(Collectors.toList());

            return RoomDTO.builder()
                    .roomNumber(room.getRoomNumber())
                    .roomType(room.getRoomType())
                    .capacityPeople(room.getCapacityPeople())
                    .minPrice(room.getPrice().intValue())
                    .bedType(room.getBedType())
                    .images(currentRoomImages)
                    .build();
        }).collect(Collectors.toList());

        return PublishingDTO.builder()
                .hotelName(place.getName())
                .description(place.getDescription())
                .checkIn(place.getCheckIn().toString())
                .checkOut(place.getCheckOut().toString())
                .capacityRoom(place.getCapacityRoom())
                .CategoryId(place.getCategory().getId())
                .addressList(addressDTOs)
                .hotelImages(hotelImageDTOs)
                .rooms(roomDTOs)
                .build();
    }
}