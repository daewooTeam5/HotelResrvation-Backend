package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.payment.projection.RoomInfoProjection;
import daewoo.team5.hotelreservation.domain.place.repository.RoomRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomInfoProjection getRoomByIdForValidate(Long roomId, String checkIn, String checkOut) {
        try {
            Optional<RoomInfoProjection> room = roomRepository.findRoomAvailability(roomId, checkIn, checkOut);
            if (room.isEmpty()) {
                System.out.println("DB 쿼리 결과 없음 (Optional 비어 있음)");
            }
            return room.orElse(null); // 필요에 따라 null 반환
        } catch (Exception e) {
            // DB에서 터진 실제 오류 메시지 확인
            e.printStackTrace();
            throw e; // 필요하면 다시 던지기
        }
    }

}
