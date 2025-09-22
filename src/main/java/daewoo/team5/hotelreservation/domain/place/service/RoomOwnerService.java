package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.RoomOwnerDTO;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomOwnerService {

    private final RoomRepository roomRepository;
    private final PlaceRepository placeRepository;

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

    public RoomOwnerDTO createRoom(Long ownerId, RoomOwnerDTO dto) {
        // ownerId로 소유자의 숙소 1개 가져오기 (여러 개 나오면 첫 번째만)
        Places place = placeRepository.findFirstByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 소유자의 숙소를 찾을 수 없습니다."));

        Room room = toEntity(dto, place);
        return toDTO(roomRepository.save(room));
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
