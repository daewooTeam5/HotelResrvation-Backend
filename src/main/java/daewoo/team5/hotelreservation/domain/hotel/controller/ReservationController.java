package daewoo.team5.hotelreservation.domain.hotel.controller;

import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationDTO;
import daewoo.team5.hotelreservation.domain.hotel.service.ReservationService;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 예약 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/reservations") // API의 기본 경로
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성 (POST)
    @PostMapping
    public ApiResult<Map<String, String>> createReservation(@RequestBody ReservationDTO reservationDTO) {
        // JWT 인증 없이 예약 생성 처리
        ReservationDTO createdReservation = reservationService.createReservation(reservationDTO);
        return new ApiResult<Map<String, String>>()
                .status(201)
                .message("Created")
                .success(true)
                .data(Map.of(
                        "reservationId", String.valueOf(createdReservation.getReservationId())
                ));
    }

    // 예약 조회 (예약 ID로) (GET)
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long reservationId) {
        Optional<ReservationDTO> reservationDTO = reservationService.getReservationById(reservationId);
        return reservationDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); // 예약이 존재하지 않으면 404 반환
    }

    // 예약자 ID로 예약 조회 (GET)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUserId(@PathVariable int userId) {
        List<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations); // 예약 목록 반환
    }
}
