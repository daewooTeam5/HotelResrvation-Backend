package daewoo.team5.hotelreservation.domain.place.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.place.dto.RoomDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomOwnerDTO;
import daewoo.team5.hotelreservation.domain.place.dto.RoomUpdateDTO;
import daewoo.team5.hotelreservation.domain.place.service.RoomOwnerService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthUser
    public ResponseEntity<?> createRoom(
            @RequestPart("data") String data,
           @RequestPart(value = "roomImages", required = false) List<MultipartFile> roomImages,
            UserProjection user
    ) throws JsonProcessingException {
        log.info("{} data----1",data);

        // JSON → DTO 변환
        ObjectMapper mapper = new ObjectMapper();
        RoomUpdateDTO dto = mapper.readValue(data, RoomUpdateDTO.class);
        log.info("{} data----1",dto);
        // 서비스 호출
        roomService.createRoom(user.getId(), dto, roomImages);

        return ResponseEntity.ok("객실 등록 성공");
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
