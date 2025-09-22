package daewoo.team5.hotelreservation.domain.place.repository;

import daewoo.team5.hotelreservation.domain.place.entity.DailyPlaceReservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyPlaceReservationRepository extends JpaRepository<DailyPlaceReservation, Long> {

    // 특정 roomId의 기간별 재고 조회
    List<DailyPlaceReservation> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    // 특정 roomId + 날짜 기준 단건 조회 (재고 차감/복구 시 동시성 제어용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DailyPlaceReservation d WHERE d.room.id = :roomId AND d.date = :date")
    Optional<DailyPlaceReservation> findByRoomIdAndDateForUpdate(@Param("roomId") Long roomId, @Param("date") LocalDate date);

    // 단순 조회용 (락 없이)
    Optional<DailyPlaceReservation> findByRoomIdAndDate(Long roomId, LocalDate date);
}
