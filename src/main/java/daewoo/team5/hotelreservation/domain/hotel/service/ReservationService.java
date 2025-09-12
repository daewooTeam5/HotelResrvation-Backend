package daewoo.team5.hotelreservation.domain.hotel.service;

import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationDTO;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.hotel.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    // 예약 생성
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        Reservation reservation = Reservation.builder()
                .userId(reservationDTO.getUserId()) // userId는 int로 처리
                .roomId(reservationDTO.getRoomId())
                .status(Reservation.Status.valueOf(reservationDTO.getStatus())) // Status 변환
                .amount(reservationDTO.getAmount())
                .resevStart(reservationDTO.getResevStart())
                .resevEnd(reservationDTO.getResevEnd())
                .paymentStatus(Reservation.PaymentStatus.valueOf(reservationDTO.getPaymentStatus())) // PaymentStatus 변환
                .createdAt(reservationDTO.getCreatedAt())
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.toDTO(); // 엔티티를 DTO로 변환하여 반환
    }

    // 예약 조회 (예약 ID로)
    public Optional<ReservationDTO> getReservationById(Long reservationId) {
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        return reservation.map(Reservation::toDTO); // 엔티티를 DTO로 변환하여 반환
    }

    // 예약자 ID로 예약 조회
    public List<ReservationDTO> getReservationsByUserId(int userId) { // userId는 int로 처리
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservations.stream().map(Reservation::toDTO).toList(); // 리스트를 DTO로 변환
    }
}
