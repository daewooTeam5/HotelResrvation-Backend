package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.RoomInventoryDTO;
import daewoo.team5.hotelreservation.domain.place.entity.DailyPlaceReservation;
import daewoo.team5.hotelreservation.domain.place.entity.Room;
import daewoo.team5.hotelreservation.domain.place.repository.DailyPlaceReservationRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomInventoryService {

    private final DailyPlaceReservationRepository reservationRepository;
    private final RoomRepository roomRepository;

    // 재고 조회
    public List<RoomInventoryDTO> getInventory(Long roomId, LocalDate start, LocalDate end) {
        return reservationRepository.findByRoomIdAndDateBetween(roomId, start, end).stream()
                .map(dpr -> new RoomInventoryDTO(dpr.getRoom().getId(), dpr.getDate(), dpr.getAvailableRoom()))
                .toList();
    }

    // 재고 수정 (없는 경우 생성)
    public RoomInventoryDTO updateInventory(Long roomId, LocalDate date, Integer availableRoom) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당 객실 유형이 존재하지 않습니다."));

        DailyPlaceReservation reservation = reservationRepository.findByRoomIdAndDate(roomId, date)
                .orElse(DailyPlaceReservation.builder()
                        .room(room)
                        .date(date)
                        .availableRoom(room.getCapacityRoom()) // 기본값: 객실 총 수
                        .build());

        reservation.setAvailableRoom(availableRoom);
        DailyPlaceReservation saved = reservationRepository.save(reservation);

        return new RoomInventoryDTO(saved.getRoom().getId(), saved.getDate(), saved.getAvailableRoom());
    }
}
