package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.payment.projection.RoomInfoProjection;
import daewoo.team5.hotelreservation.domain.place.service.RoomService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/rooms")
public class RoomController {
    private final RoomService roomService;

    @GetMapping("/{id}")
    public ApiResult<RoomInfoProjection> getRoomById(
            @PathVariable("id") Long roomId,
            @RequestParam String checkIn,
            @RequestParam String checkOut
    ) {

        return ApiResult.ok(
                roomService.getRoomByIdForValidate(roomId, checkIn, checkOut),
                "객실 정보 조회 성공"
        );

    }
}
