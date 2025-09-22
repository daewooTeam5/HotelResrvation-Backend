package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.*;
import daewoo.team5.hotelreservation.domain.place.service.ReservationService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * 예약 관리 API 컨트롤러 (숙소 주인용)
 */
@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Slf4j
public class PlaceOwnerController {

    private final ReservationService reservationService;

    /**
     * 예약 목록 조회
     * GET /api/v1/reservations?ownerId=1&page=0&size=5
     */
    @GetMapping
    @AuthUser
    public ResponseEntity<Page<ReservationListDTO>> getAllReservations(
            UserProjection projection,
            Pageable pageable) {
        return ResponseEntity.ok(reservationService.getAllReservations(projection.getId(), pageable));
    }

    /**
     * 예약 상세 조회
     * GET /api/v1/reservations/{reservationId}?ownerId=1
     */
    @GetMapping("/{reservationId}")
    @AuthUser
    public ResponseEntity<ReservationDetailDTO> getReservationById(
            @PathVariable Long reservationId,
            UserProjection projection) {
        Optional<ReservationDetailDTO> reservationDTO =
                reservationService.getReservationById(reservationId, projection.getId());
        return reservationDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 예약 수정
     * PUT /api/v1/reservations/{reservationId}?ownerId=1
     */
    @PutMapping("/{reservationId}")
    @AuthUser
    public ResponseEntity<ReservationDetailDTO> updateReservation(
            @PathVariable Long reservationId,
            UserProjection projection,
            @RequestBody ReservationRequestDTO requestDTO) {
        ReservationDetailDTO updated = reservationService.updateReservation(reservationId, projection.getId(), requestDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    /**
     * 예약 취소 (+ 객실 available 복구)
     * PUT /api/v1/reservations/{reservationId}/cancel?ownerId=1
     */
    @PutMapping("/{reservationId}/cancel")
    @AuthUser
    public ResponseEntity<ReservationDetailDTO> cancel(
            @PathVariable Long reservationId,
            UserProjection projection) {
        ReservationDetailDTO cancelled = reservationService.cancel(reservationId, projection.getId());
        return ResponseEntity.ok(cancelled);
    }

    /**
     * 예약 검색 / 필터
     * POST /api/v1/reservations/search?ownerId=1&page=0&size=5
     */
    @PostMapping("/search")
    @AuthUser
    public ResponseEntity<Page<ReservationListDTO>> searchReservations(
            @RequestBody ReservationSearchRequest request,
            Pageable pageable, UserProjection projection) {
        return ResponseEntity.ok(reservationService.searchReservations(request, projection.getId(), pageable));
    }

    @PostMapping("/test")
    @AuthUser
    public ResponseEntity<ReservationDetailDTO> createTestReservation(
            @RequestBody ReservationTestRequestDTO dto,
            UserProjection projection
    ) {
        return ResponseEntity.ok(reservationService.createTestReservation(projection.getId(), dto));
    }

}
