package daewoo.team5.hotelreservation.domain.hotel.service;

import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationDTO;
import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationResponse;
import daewoo.team5.hotelreservation.domain.hotel.dto.ReservationUpdateRequest;
import daewoo.team5.hotelreservation.domain.hotel.entity.Room;
import daewoo.team5.hotelreservation.domain.hotel.entity.RoomNo;
import daewoo.team5.hotelreservation.domain.hotel.repository.PaymentRepository;
import daewoo.team5.hotelreservation.domain.hotel.repository.RoomNoRepository;
import daewoo.team5.hotelreservation.domain.hotel.repository.RoomRepository;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.hotel.repository.ReservationRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomNoRepository roomNoRepository;

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
            reservation.setStatus(Reservation.ReservationStatus.valueOf(reservationDTO.getStatus()));
            reservation.setAmount(reservationDTO.getAmount());
            reservation.setResevStart(reservationDTO.getResevStart());
            reservation.setResevEnd(reservationDTO.getResevEnd());
            reservation.setPaymentStatus(Reservation.ReservationPaymentStatus.valueOf(reservationDTO.getPaymentStatus()));
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

    @Transactional
    public ReservationResponse update(Long reservationId, ReservationUpdateRequest req) {
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(404, "Not Found", "예약을 찾을 수 없습니다."));

        if (req.getRoomId() != null)        r.setRoomId(req.getRoomId());
        if (req.getAmount() != null)        r.setAmount(req.getAmount());
        if (req.getResevStart() != null)    r.setResevStart(req.getResevStart());
        if (req.getResevEnd() != null)      r.setResevEnd(req.getResevEnd());

        if (req.getStatus() != null) {
            r.setStatus(Reservation.ReservationStatus.valueOf(req.getStatus())); // 정확한 이름 필요
        }
        if (req.getPaymentStatus() != null) {
            r.setPaymentStatus(Reservation.ReservationPaymentStatus.valueOf(req.getPaymentStatus()));
        }

        Reservation saved = reservationRepository.save(r);
        return toResponse(saved);
    }

    @Transactional
    public ReservationResponse cancel(Long reservationId) {
        // 1) 예약 조회
        Reservation r = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(404, "Not Found", "예약을 찾을 수 없습니다."));

        // 2) 상태 변경(생성일은 그대로)
        r.setStatus(Reservation.ReservationStatus.cancelled);
        if (r.getPaymentStatus() != null) {
            r.setPaymentStatus(Reservation.ReservationPaymentStatus.refunded);
        }

        // 3) room_id 매핑 (숫자면 그대로, 아니면 room_no에서 매핑)
        Long roomPk = resolveRoomPk(r.getRoomId());

        // 4) 객실 상태 available
        Room room = roomRepository.findById(roomPk)
                .orElseThrow(() -> new ApiException(404, "Not Found", "객실을 찾을 수 없습니다. id=" + roomPk));
        room.setStatus("available");

        // 5) 저장 & 응답
        Reservation saved = reservationRepository.save(r);
        return toResponse(saved);
    }

    private Long resolveRoomPk(String roomId) {
        if (roomId == null || roomId.isBlank()) {
            throw new ApiException(400, "Bad Request", "roomId가 비어 있습니다.");
        }
        try {
            return Long.valueOf(roomId);
        } catch (NumberFormatException ignored) {
            RoomNo rn = roomNoRepository.findByRoomNo(roomId)
                    .orElseThrow(() -> new ApiException(500, "서버 오류",
                            "room_no에서 객실 매핑 실패: roomId=" + roomId));
            Long pk = rn.getRoomId(); // <- 변경 포인트 (getRoom() 아님)
            if (pk == null) {
                throw new ApiException(500, "서버 오류",
                        "room_no 매핑은 있었지만 rooms.id를 찾을 수 없습니다.");
            }
            return pk;
        }
    }

    // Reservation -> ReservationResponse 매핑 (toResponse(saved) 오류 해결용)
    private ReservationResponse toResponse(Reservation r) {
        return new ReservationResponse(
                r.getReservationId(),
                r.getRoomId(),
                r.getUserId(),
                r.getStatus() != null ? r.getStatus().name() : null,
                r.getAmount(),
                r.getResevStart(),
                r.getResevEnd(),
                r.getCreatedAt()
        );
    }
}
