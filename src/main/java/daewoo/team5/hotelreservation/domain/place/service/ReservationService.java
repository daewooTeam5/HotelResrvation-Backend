package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.payment.entity.Payment;
import daewoo.team5.hotelreservation.domain.payment.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.place.dto.*;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.specification.ReservationSpecification;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

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
        }).orElseThrow(() -> new ApiException(404, "Not Found", "해당 소유자의 예약을 찾을 수 없습니다."));
    }

    // 소유자 기반 예약 취소
    @Transactional
    public ReservationDetailDTO cancel(Long reservationId, Long ownerId) {
        Reservation r = reservationRepository.findByIdAndOwnerId(reservationId, ownerId)
                .orElseThrow(() -> new ApiException(404, "Not Found", "해당 소유자의 예약을 찾을 수 없습니다."));

        r.setStatus(Reservation.ReservationStatus.cancelled);
        r.setPaymentStatus(Reservation.ReservationPaymentStatus.refunded);

        Room room = r.getRoom();
        if (room != null) {
            room.setStatus(Room.Status.AVAILABLE);
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
}
