package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.file.service.FileService;
import daewoo.team5.hotelreservation.domain.place.dto.RoomDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomOwnerDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomUpdateDTO;
import daewoo.team5.hotelreservation.domain.place.entity.Amenity;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.entity.RoomAmenityEntity;
import daewoo.team5.hotelreservation.domain.place.repository.AmenityRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomAmenityRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomOwnerService {

    private final RoomRepository roomRepository;
    private final PlaceRepository placeRepository;
    private final FileService fileService;
    private final RoomAmenityRepository roomAmenityRepository;
    private final AmenityRepository amenityRepository;

    public List<RoomOwnerDTO> getRoomsByOwner(Long ownerId) {
        return roomRepository.findAllByOwnerId(ownerId).stream()
                .map(this::toDTO)
                .toList();
    }

    public RoomOwnerDTO getRoom(Long ownerId, Long roomId) {
        Room room = roomRepository.findByIdAndOwnerId(roomId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 객실 유형을 찾을 수 없거나 권한이 없습니다."));
        return toDTO(room);
    }

    @Transactional
    public RoomDTO createRoom(Long ownerId, RoomUpdateDTO dto, List<MultipartFile> roomImages) {
        // ✅ ownerId로 Place 조회
        Places place = placeRepository.findByOwner_Id(ownerId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "숙소 없음", "ownerId=" + ownerId));

        // ✅ Room 엔티티 생성
        Room room = Room.builder()
                .roomType(dto.getRoomType())
                .bedType(dto.getBedType())
                .capacityPeople(dto.getCapacityPeople())
                .capacityRoom(dto.getCapacityRoom())
                .price(BigDecimal.valueOf(dto.getMinPrice()))
                .status(Room.Status.AVAILABLE)
                .place(place)  // 🔑 ownerId로 매핑된 place
                .build();
        roomRepository.save(room);

        // ✅ 편의시설 매핑
        if (dto.getAmenityIds() != null && !dto.getAmenityIds().isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllById(dto.getAmenityIds());
            List<RoomAmenityEntity> entities = amenities.stream()
                    .map(a -> RoomAmenityEntity.builder()
                            .room(room)
                            .amenity(a)
                            .build())
                    .toList();
            roomAmenityRepository.saveAll(entities);
        }

        // ✅ 이미지 업로드
        if (roomImages != null) {
            for (MultipartFile img : roomImages) {
                if (img != null && !img.isEmpty()) {
                    fileService.uploadAndSave(img, ownerId, room.getId(), "room", null);
                }
            }
        }

        // ✅ DTO로 변환해서 반환
        return RoomDTO.builder()
                .roomNumber(room.getId().intValue())
                .roomType(room.getRoomType())
                .capacityPeople(room.getCapacityPeople())
                .capacityRoom(room.getCapacityRoom())
                .minPrice(room.getPrice().intValue())
                .bedType(room.getBedType())
                //.images(dto.getImages())  // 필요하면 매핑
                .amenityIds(dto.getAmenityIds())
                .build();
    }


    public RoomOwnerDTO updateRoom(Long ownerId, Long roomId, RoomOwnerDTO dto) {
        Room room = roomRepository.findByIdAndOwnerId(roomId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 객실 유형을 찾을 수 없거나 권한이 없습니다."));

        room.setRoomType(dto.getRoomType());
        room.setBedType(dto.getBedType());
        room.setCapacityPeople(dto.getCapacityPeople());
        room.setCapacityRoom(dto.getCapacityRoom());
        room.setPrice(dto.getPrice());
        room.setStatus(dto.getStatus());

        return toDTO(roomRepository.save(room));
    }

    public void deleteRoom(Long ownerId, Long roomId) {
        Room room = roomRepository.findByIdAndOwnerId(roomId, ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 객실 유형을 찾을 수 없거나 권한이 없습니다."));
        roomRepository.delete(room);
    }

    private RoomOwnerDTO toDTO(Room room) {
        return RoomOwnerDTO.builder()
                .id(room.getId())
                .placeId(room.getPlace().getId())
                .roomType(room.getRoomType())
                .bedType(room.getBedType())
                .capacityPeople(room.getCapacityPeople())
                .capacityRoom(room.getCapacityRoom())
                .price(room.getPrice())
                .status(room.getStatus())
                .build();
    }

    private Room toEntity(RoomOwnerDTO dto, Places place) {
        return Room.builder()
                .place(place)
                .roomType(dto.getRoomType())
                .bedType(dto.getBedType())
                .capacityPeople(dto.getCapacityPeople())
                .capacityRoom(dto.getCapacityRoom())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .build();
    }


}
