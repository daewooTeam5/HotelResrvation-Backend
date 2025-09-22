package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.RoomInventoryDTO;
import daewoo.team5.hotelreservation.domain.place.service.RoomInventoryService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/owner/inventory")
@RequiredArgsConstructor
public class RoomInventoryController {

    private final RoomInventoryService inventoryService;

    /**
     * 특정 객실의 기간별 재고 조회
     * GET /api/v1/owner/inventory/{roomId}?start=2025-09-20&end=2025-09-30
     */
    @GetMapping("/{roomId}")
    @AuthUser
    public ResponseEntity<List<RoomInventoryDTO>> getInventory(
            @PathVariable Long roomId,
            @RequestParam LocalDate start,
            @RequestParam LocalDate end,
            UserProjection projection) {
        // TODO: ownerId 검증 (roomId가 projection.getId() 소유자인지 확인)
        return ResponseEntity.ok(inventoryService.getInventory(roomId, start, end));
    }

    /**
     * 특정 객실 날짜의 재고 수정
     * PUT /api/v1/owner/inventory/{roomId}
     */
    @PutMapping("/{roomId}")
    @AuthUser
    public ResponseEntity<RoomInventoryDTO> updateInventory(
            @PathVariable Long roomId,
            @RequestBody RoomInventoryDTO dto,
            UserProjection projection) {
        // TODO: ownerId 검증
        return ResponseEntity.ok(inventoryService.updateInventory(roomId, dto.getDate(), dto.getAvailableRoom()));
    }
}
