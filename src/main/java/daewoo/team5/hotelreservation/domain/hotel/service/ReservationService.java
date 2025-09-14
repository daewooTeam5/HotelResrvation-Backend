package daewoo.team5.hotelreservation.domain.hotel.service;

import daewoo.team5.hotelreservation.domain.hotel.dto.*;
import daewoo.team5.hotelreservation.domain.hotel.entity.Room;
import daewoo.team5.hotelreservation.domain.hotel.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.hotel.specification.ReservationSpecification;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.hotel.repository.ReservationRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    // 예약 목록 조회
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservation -> new ReservationDTO(
                        reservation.getReservationId(),
                        reservation.getUser().getId(),
                        reservation.getRoomNoString(), // 사람이 보는 객실 번호
                        reservation.getStatus().name(),
                        reservation.getAmount(),
                        reservation.getResevStart().toString(),
                        reservation.getResevEnd().toString(),
                        reservation.getPaymentStatus().name(),
                        reservation.getCreatedAt()
                ))
                .toList();
    }

    // 예약 상세 조회
    public Optional<ReservationDTO> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> new ReservationDTO(
                        reservation.getReservationId(),
                        reservation.getUser().getId(),
                        reservation.getRoomNoString(),
                        reservation.getStatus().name(),
                        reservation.getAmount(),
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
            reservation.setAmount(reservationDTO.getAmount());
            reservation.setResevStart(LocalDateTime.parse(reservationDTO.getResevStart()));
            reservation.setResevEnd(LocalDateTime.parse(reservationDTO.getResevEnd()));
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
            room.setStatus("available");
        }

        Reservation saved = reservationRepository.save(r);
        return toResponse(saved);
    }

    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getReservationId(),
                r.getRoomNoString(),
                r.getUser().getId(),
                r.getStatus() != null ? r.getStatus().name() : null,
                r.getAmount(),
                r.getResevStart().toString(),
                r.getResevEnd().toString(),
                r.getCreatedAt()
        );
    }

    // 검색
    public List<ReservationSearchResponse> searchReservations(ReservationSearchRequest req) {
        List<Reservation> reservations = reservationRepository.findAll(
                ReservationSpecification.filter(req)
        );

        return reservations.stream().map(r -> ReservationSearchResponse.builder()
                .reservationId(r.getReservationId())
                .userName(r.getUser() != null ? r.getUser().getName() : null)
                .email(r.getUser() != null ? r.getUser().getEmail() : null)
                .roomNo(r.getRoomNoString())
                .hotelName(
                        (r.getRoom() != null && r.getRoom().getPlace() != null)
                                ? r.getRoom().getPlace().getName()
                                : null
                )
                .resevStart(r.getResevStart().toString())
                .resevEnd(r.getResevEnd().toString())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .paymentStatus(r.getPaymentStatus() != null ? r.getPaymentStatus().name() : null)
                .amount(r.getAmount())
                .createdAt(r.getCreatedAt())
                .build()
        ).toList();
    }
}

