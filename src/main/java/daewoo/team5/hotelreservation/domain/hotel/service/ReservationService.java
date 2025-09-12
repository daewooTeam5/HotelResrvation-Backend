package daewoo.team5.hotelreservation.domain.hotel.service;

import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationDTO;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.hotel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    // 예약 목록 조회
    public List<ReservationDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(reservation -> new ReservationDTO(
                        reservation.getReservationId(),
                        reservation.getUserId(),
                        reservation.getRoomId(),
                        reservation.getStatus().name(),
                        reservation.getAmount(),
                        reservation.getResevStart(),
                        reservation.getResevEnd(),
                        reservation.getPaymentStatus().name(),
                        reservation.getCreatedAt()
                ))
                .collect(Collectors.toList());  // 예약 목록을 DTO로 변환하여 반환
    }

    // 예약 상세 조회 (예약 ID로)
    public Optional<ReservationDTO> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(reservation -> new ReservationDTO(
                        reservation.getReservationId(),
                        reservation.getUserId(),
                        reservation.getRoomId(),
                        reservation.getStatus().name(),
                        reservation.getAmount(),
                        reservation.getResevStart(),
                        reservation.getResevEnd(),
                        reservation.getPaymentStatus().name(),
                        reservation.getCreatedAt()
                ));
    }

    // 예약 수정 (예약 ID로)
    public ReservationDTO updateReservation(Long reservationId, ReservationDTO reservationDTO) {
        // 예약 ID로 예약을 조회
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
        if (reservationOptional.isPresent()) {
            Reservation reservation = reservationOptional.get();

            // 예약 정보를 수정
            reservation.setRoomId(reservationDTO.getRoomId());
            reservation.setStatus(Reservation.Status.valueOf(reservationDTO.getStatus()));
            reservation.setAmount(reservationDTO.getAmount());
            reservation.setResevStart(reservationDTO.getResevStart());
            reservation.setResevEnd(reservationDTO.getResevEnd());
            reservation.setPaymentStatus(Reservation.PaymentStatus.valueOf(reservationDTO.getPaymentStatus()));
            reservation.setCreatedAt(reservationDTO.getCreatedAt());

            // 수정된 예약 정보 저장
            reservationRepository.save(reservation);

            // DTO로 변환하여 반환
            return new ReservationDTO(
                    reservation.getReservationId(),
                    reservation.getUserId(),
                    reservation.getRoomId(),
                    reservation.getStatus().name(),
                    reservation.getAmount(),
                    reservation.getResevStart(),
                    reservation.getResevEnd(),
                    reservation.getPaymentStatus().name(),
                    reservation.getCreatedAt()
            );
        }
        return null;  // 예약이 존재하지 않으면 null 반환
    }
}
