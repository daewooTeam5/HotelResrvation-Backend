package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.payment.dto.TossCancelResponse;
import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.projection.ReservationInfoProjection;
import daewoo.team5.hotelreservation.domain.payment.repository.GuestRepository;
import daewoo.team5.hotelreservation.domain.payment.service.TossPaymentService;
import daewoo.team5.hotelreservation.domain.place.dto.*;
import daewoo.team5.hotelreservation.domain.place.entity.DailyPlaceReservation;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.DailyPlaceReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.place.specification.ReservationSpecification;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final DailyPlaceReservationRepository dailyPlaceReservationRepository;
    private final TossPaymentService tossPaymentService;
    private final GuestRepository guestRepository;
    /**
     * ✅ [추가] 리뷰 작성 가능한 예약 목록을 조회하는 서비스 로직
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<ReviewableReservationResponse> getReviewableReservations(Long placeId, UserProjection user) {
        if (user == null) {
            return List.of(); // 비로그인 시 빈 목록 반환
        }
        GuestEntity guest = guestRepository.findByUsersId(user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "투숙객 정보를 찾을 수 없습니다.","투숙객 정보를 찾을 수 없습니다."));

        return reservationRepository.findReviewableReservations(guest.getId(), placeId);
    }
    // ===================== 변환 메서드 =====================

    private ReservationListDTO toListDTO(Reservation r) {
        return ReservationListDTO.builder()
                .reservationId(r.getReservationId())
                .guestName(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getName()
                        : r.getGuest().getFirstName() + " " + r.getGuest().getLastName())
                        : null)
                .roomType(r.getRoom() != null ? r.getRoom().getRoomType() : null)
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .paymentStatus(r.getPaymentStatus() != null ? r.getPaymentStatus().name() : null)
                .resevStart(r.getResevStart())
                .resevEnd(r.getResevEnd())
                .finalAmount(r.getFinalAmount())
                .createdAt(r.getCreatedAt())
                .member(r.getGuest() != null && r.getGuest().getUsers() != null)
                .build();
    }

    private ReservationDetailDTO toDetailDTO(Reservation r) {
        Optional<Payment> paymentOpt =
                paymentRepository.findTop1ByReservation_ReservationIdOrderByTransactionDateDesc(r.getReservationId());

        return ReservationDetailDTO.builder()
                .reservationId(r.getReservationId())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .paymentStatus(r.getPaymentStatus() != null ? r.getPaymentStatus().name() : null)
                .createdAt(r.getCreatedAt())
                .request(r.getRequest())

                // 예약자 정보
                .userId(r.getGuest() != null && r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getId()
                        : null)
                .guestId(r.getGuest() != null ? r.getGuest().getId() : null)
                .guestName(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getName()
                        : r.getGuest().getFirstName() + " " + r.getGuest().getLastName())
                        : null)
                .email(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getEmail()
                        : r.getGuest().getEmail())
                        : null)
                .phone(r.getGuest() != null
                        ? (r.getGuest().getUsers() != null
                        ? r.getGuest().getUsers().getPhone()
                        : r.getGuest().getPhone())
                        : null)
                .member(r.getGuest() != null && r.getGuest().getUsers() != null)

                // 객실 정보
                .roomId(r.getRoom() != null ? r.getRoom().getId() : null)
                .roomType(r.getRoom() != null ? r.getRoom().getRoomType() : null)
                .capacityPeople(r.getRoom() != null ? r.getRoom().getCapacityPeople() : null)
                .price(r.getRoom() != null ? r.getRoom().getPrice() : null)

                // 예약 기간 및 금액
                .resevStart(r.getResevStart())
                .resevEnd(r.getResevEnd())
                .resevAmount(r.getResevAmount())
                .baseAmount(r.getBaseAmount())
                .finalAmount(r.getFinalAmount())

                // 결제 정보
                .paymentId(paymentOpt.map(Payment::getId).orElse(null))
                .method(paymentOpt.map(p -> p.getMethod().name()).orElse(null))
                .paymentStatusDetail(paymentOpt.map(p -> p.getStatus().name()).orElse(null))
                .paymentAmount(paymentOpt.map(Payment::getAmount).orElse(null))
                .transactionDate(paymentOpt.map(Payment::getTransactionDate).orElse(null))

                .build();
    }

    // ===================== 서비스 메서드 =====================

    // 소유자 기반 예약 목록 조회
    public Page<ReservationListDTO> getAllReservations(Long ownerId, Pageable pageable) {
        return reservationRepository.findAllByOwnerId(ownerId, pageable).map(this::toListDTO);
    }

    // 소유자 기반 예약 상세 조회
    public Optional<ReservationDetailDTO> getReservationById(Long reservationId, Long ownerId) {
        return reservationRepository.findByIdAndOwnerId(reservationId, ownerId).map(this::toDetailDTO);
    }

    // 소유자 기반 예약 수정
    @Transactional
    public ReservationDetailDTO updateReservation(Long reservationId, Long ownerId, ReservationRequestDTO dto) {
        return reservationRepository.findByIdAndOwnerId(reservationId, ownerId).map(reservation -> {
            if (dto.getStatus() != null) {
                reservation.setStatus(Reservation.ReservationStatus.valueOf(dto.getStatus()));
            }
            if (dto.getPaymentStatus() != null) {
                reservation.setPaymentStatus(Reservation.ReservationPaymentStatus.valueOf(dto.getPaymentStatus()));
            }
            if (dto.getResevStart() != null) {
                reservation.setResevStart(dto.getResevStart());
            }
            if (dto.getResevEnd() != null) {
                reservation.setResevEnd(dto.getResevEnd());
            }
            Reservation saved = reservationRepository.save(reservation);
            return toDetailDTO(saved);
        }).orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                "해당 소유자의 예약을 찾을 수 없습니다."
        ));
    }

    // 소유자 기반 예약 취소
    @Transactional
    public ReservationDetailDTO cancel(Long reservationId, Long ownerId) {
        Reservation r = reservationRepository.findByIdAndOwnerId(reservationId, ownerId)
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        "해당 소유자의 예약을 찾을 수 없습니다."
                ));

        // ✅ 결제 정보 확인
        Payment payment = paymentRepository
                .findTop1ByReservation_ReservationIdOrderByTransactionDateDesc(r.getReservationId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "결제 정보 없음",
                        "해당 예약의 결제 내역을 찾을 수 없습니다."
                ));

        // ✅ 토스 환불 API 호출
        TossCancelResponse response = tossPaymentService.cancelPayment(payment.getPaymentKey(), "고객 예약 취소");

        // ✅ DB 업데이트
        r.setStatus(Reservation.ReservationStatus.cancelled);
        r.setPaymentStatus(Reservation.ReservationPaymentStatus.refunded);

        payment.setStatus(Payment.PaymentStatus.refunded);
        if (response != null && response.getCancels() != null && !response.getCancels().isEmpty()) {
            TossCancelResponse.CancelHistory lastCancel = response.getCancels().get(response.getCancels().size() - 1);
            payment.setAmount(lastCancel.getCancelAmount()); // 환불 금액 반영
            payment.setTransactionDate(lastCancel.getCanceledAt().toLocalDateTime()); // 환불 시각 반영
        }
        paymentRepository.save(payment);

        // ✅ 재고 복구
        if (r.getRoom() != null && r.getResevStart() != null && r.getResevEnd() != null) {
            adjustInventory(r.getRoom().getId(), r.getResevStart(), r.getResevEnd(), +1);
        }

        Reservation saved = reservationRepository.save(r);
        return toDetailDTO(saved);
    }

    // 소유자 기반 검색
    public Page<ReservationListDTO> searchReservations(ReservationSearchRequest req, Long ownerId, Pageable pageable) {
        return reservationRepository.findAll(
                ReservationSpecification.filter(req, ownerId), pageable
        ).map(this::toListDTO);
    }

    // ===================== 재고 관리 연동 =====================

    /**
     * 재고 조정 유틸 메서드
     * @param roomId 객실 ID
     * @param start 예약 시작일
     * @param end 예약 종료일
     * @param delta 변경 수량 (+1 복구, -1 차감)
     */
    private void adjustInventory(Long roomId, LocalDate start, LocalDate end, int delta) {
        LocalDate date = start;
        while (!date.isAfter(end)) {
            LocalDate currentDate = date; // 🔑 새 변수로 캡처

            DailyPlaceReservation dpr = dailyPlaceReservationRepository
                    .findByRoomIdAndDateForUpdate(roomId, currentDate)
                    .orElseThrow(() -> new ApiException(
                            HttpStatus.NOT_FOUND,
                            "재고 없음",
                            "해당 날짜(" + currentDate + ")에 재고가 존재하지 않습니다."
                    ));

            int updated = dpr.getAvailableRoom() + delta;
            if (updated < 0) {
                throw new ApiException(
                        HttpStatus.BAD_REQUEST,
                        "재고 부족",
                        "선택한 날짜(" + currentDate + ")에 재고가 부족합니다."
                );
            }
            dpr.setAvailableRoom(updated);
            dailyPlaceReservationRepository.save(dpr);

            date = date.plusDays(1);
        }
    }

    @Transactional
    public ReservationDetailDTO createTestReservation(Long ownerId, ReservationTestRequestDTO dto) {
        // ✅ 객실 확인
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ApiException(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        "해당 객실 유형을 찾을 수 없습니다."
                ));

        // ✅ 소유자 검증
        if (!room.getPlace().getOwner().getId().equals(ownerId)) {
            throw new ApiException(
                    HttpStatus.FORBIDDEN,
                    "Forbidden",
                    "해당 객실은 현재 소유자의 소유가 아닙니다."
            );
        }

        // ✅ 예약 엔티티 생성
        Reservation reservation = Reservation.builder()
                .room(room)
                .status(Reservation.ReservationStatus.confirmed)   // 테스트니까 바로 확정
                .paymentStatus(Reservation.ReservationPaymentStatus.paid)
                .resevStart(dto.getResevStart())
                .resevEnd(dto.getResevEnd())
                .baseAmount(dto.getBaseAmount())
                .finalAmount(dto.getFinalAmount())
                .request(dto.getRequest())
                .build();

        Reservation saved = reservationRepository.save(reservation);

        // ✅ 재고 차감
        if (saved.getRoom() != null && saved.getResevStart() != null && saved.getResevEnd() != null) {
            adjustInventory(
                    saved.getRoom().getId(),
                    saved.getResevStart(),
                    saved.getResevEnd(),
                    -1
            );
        }

        return toDetailDTO(saved);
    }

    /**
     * 주석: 사용자가 특정 숙소에 대해 리뷰를 작성할 수 있는지 확인합니다.
     * @param placeId 확인할 숙소 ID
     * @param user 현재 로그인한 사용자 정보
     * @return 리뷰 작성 가능 여부 (true/false)
     */
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean canUserWriteReview(Long placeId, UserProjection user) {
        if (user == null) {
            return false;
        }
        GuestEntity guestEntity = guestRepository.findByUsersId(user.getId()).orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Guest Not Found",
                "해당 유저의 투숙객 정보를 찾을 수 없습니다."
        ));
        // 체크아웃(checked_out) 상태의 예약이 존재하는지 확인
        return reservationRepository.existsByUsersIdAndRoomPlaceIdAndStatus(
                guestEntity.getId(),
                placeId,
                Reservation.ReservationStatus.checked_out
        );
    }

    public List<ReservationInfoProjection> getReservationsByPlaceId(Long placeId) {
        return reservationRepository.findByRoom_Place_Id(placeId);
    }

}