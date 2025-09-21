package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.place.specification.ReservationSpecification;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationDTO;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationResponse;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationSearchRequest;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationSearchResponse;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    // 예약 목록 조회
    public Page<ReservationDTO> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservation -> new ReservationDTO(
                        reservation.getReservationId(),
                        reservation.getGuest() != null ? reservation.getGuest().getId() : null,
                        reservation.getRoom() != null ? reservation.getRoom().getId().toString() : null,
                        reservation.getStatus() != null ? reservation.getStatus().name() : null,
                        reservation.getFinalAmount(),
                        reservation.getResevStart() != null ? reservation.getResevStart().toString() : null,
                        reservation.getResevEnd() != null ? reservation.getResevEnd().toString() : null,
                        reservation.getPaymentStatus() != null ? reservation.getPaymentStatus().name() : null,
                        reservation.getCreatedAt()
                ));
    }

    // 예약 상세 조회
    public Optional<ReservationDTO> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> new ReservationDTO(
                        reservation.getReservationId(),
                        reservation.getGuest().getId(),
                        reservation.getRoom().getId().toString(),
                        reservation.getStatus().name(),
                        reservation.getFinalAmount(),
                        reservation.getResevStart().toString(),
                        reservation.getResevEnd().toString(),
                        reservation.getPaymentStatus().name(),
                        reservation.getCreatedAt()
                ));
    }

    // 예약 수정
    public ReservationDTO updateReservation(Long reservationId, ReservationDTO reservationDTO) {
        return reservationRepository.findById(reservationId).map(reservation -> {
            reservation.setStatus(Reservation.ReservationStatus.valueOf(reservationDTO.getStatus()));
            reservation.setFinalAmount(reservationDTO.getAmount());
            reservation.setResevStart(LocalDate.parse(reservationDTO.getResevStart()));
            reservation.setResevEnd(LocalDate.parse(reservationDTO.getResevEnd()));
            reservation.setPaymentStatus(Reservation.ReservationPaymentStatus.valueOf(reservationDTO.getPaymentStatus()));
            reservationRepository.save(reservation);
            return reservationDTO;
        }).orElse(null);
    }

    // 예약 취소
    @Transactional
    public ReservationResponse cancel(Long reservationId) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(404, "Not Found", "예약을 찾을 수 없습니다."));

        r.setStatus(Reservation.ReservationStatus.cancelled);
        if (r.getPaymentStatus() != null) {
            r.setPaymentStatus(Reservation.ReservationPaymentStatus.refunded);
        }

        Room room = r.getRoom();
        if (room != null) {
            room.setStatus(Room.Status.AVAILABLE);
        }

        Reservation saved = reservationRepository.save(r);
        return toResponse(saved);
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getReservationId(),
                r.getRoom().getId().toString(),
                r.getGuest().getId(),
                r.getStatus() != null ? r.getStatus().name() : null,
                r.getFinalAmount(),
                r.getResevStart().toString(),
                r.getResevEnd().toString(),
                r.getCreatedAt()
        );
    }

    // 검색
    public Page<ReservationSearchResponse> searchReservations(ReservationSearchRequest req, Pageable pageable) {
        Page<Reservation> reservations = reservationRepository.findAll(
                ReservationSpecification.filter(req), pageable
        );

        return reservations.map(r -> ReservationSearchResponse.builder()
                .reservationId(r.getReservationId())
                .userName(r.getGuest() != null ? r.getGuest().getFirstName()+r.getGuest().getLastName() : null)
                .email(r.getGuest() != null ? r.getGuest().getEmail() : null)
                .roomNo(
                        (r.getRoom() != null && r.getRoom().getId() != null && !r.getRoom().getRoomNos().isEmpty())
                                ? r.getRoom().getRoomNos().get(0).getRoomNo()
                                : null
                )
                .hotelName(
                        r.getRoom() != null && r.getRoom().getPlace() != null
                                ? r.getRoom().getPlace().getName()
                                : null
                )
                .resevStart(r.getResevStart().toString())
                .resevEnd(r.getResevEnd().toString())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .paymentStatus(r.getPaymentStatus() != null ? r.getPaymentStatus().name() : null)
                .amount(r.getFinalAmount())
                .createdAt(r.getCreatedAt())
                .build()
        );
    }
}

