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

    // 예약 목록 조회 (GET)
    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);  // 예약 목록 반환
    }

    // 예약 상세 조회 (예약 ID로) (GET)
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long reservationId) {
        Optional<ReservationDTO> reservationDTO = reservationService.getReservationById(reservationId);
        return reservationDTO.map(ResponseEntity::ok) // 예약이 존재하면 200 OK와 함께 반환
                .orElseGet(() -> ResponseEntity.notFound().build()); // 예약이 존재하지 않으면 404 반환
    }

    // 예약 수정 (PUT)
    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationDTO> updateReservation(@PathVariable Long reservationId,
                                                            @RequestBody ReservationDTO reservationDTO) {
        ReservationDTO updatedReservation = reservationService.updateReservation(reservationId, reservationDTO);
        if (updatedReservation != null) {
            return ResponseEntity.ok(updatedReservation);  // 수정된 예약 정보를 반환
        } else {
            return ResponseEntity.notFound().build();  // 예약이 없으면 404 반환
        }
    }
}
