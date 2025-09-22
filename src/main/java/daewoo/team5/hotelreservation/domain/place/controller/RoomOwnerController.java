package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.RoomOwnerDTO;
import daewoo.team5.hotelreservation.domain.place.service.RoomOwnerService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owner/rooms")
@RequiredArgsConstructor
public class RoomOwnerController {

    private final RoomOwnerService roomService;

    /**
     * 소유자의 모든 객실 유형 조회
     */
    @GetMapping
    @AuthUser
    public ResponseEntity<List<RoomOwnerDTO>> getRoomsByOwner(UserProjection projection) {
        return ResponseEntity.ok(roomService.getRoomsByOwner(projection.getId()));
    }

    /**
     * 객실 유형 상세 조회
     */
    @GetMapping("/{roomId}")
    @AuthUser
    public ResponseEntity<RoomOwnerDTO> getRoom(@PathVariable Long roomId,
                                                UserProjection projection) {
        return ResponseEntity.ok(roomService.getRoom(projection.getId(), roomId));
    }

    /**
     * 객실 유형 생성
     */
    @PostMapping
    @AuthUser
    public ResponseEntity<RoomOwnerDTO> createRoom(@RequestBody RoomOwnerDTO dto,
                                                   UserProjection projection) {
        return ResponseEntity.ok(roomService.createRoom(projection.getId(), dto));
    }

    /**
     * 객실 유형 수정
     */
    @PutMapping("/{roomId}")
    @AuthUser
    public ResponseEntity<RoomOwnerDTO> updateRoom(@PathVariable Long roomId,
                                                   @RequestBody RoomOwnerDTO dto,
                                                   UserProjection projection) {
        return ResponseEntity.ok(roomService.updateRoom(projection.getId(), roomId, dto));
    }

    /**
     * 객실 유형 삭제
     */
    @DeleteMapping("/{roomId}")
    @AuthUser
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId,
                                           UserProjection projection) {
        roomService.deleteRoom(projection.getId(), roomId);
        return ResponseEntity.noContent().build();
    }
}
