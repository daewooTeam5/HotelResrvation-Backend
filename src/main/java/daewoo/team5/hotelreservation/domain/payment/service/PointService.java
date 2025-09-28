package daewoo.team5.hotelreservation.domain.payment.service;

import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.Reservation;
import daewoo.team5.hotelreservation.domain.payment.projection.PointHistorySummaryProjection;
import daewoo.team5.hotelreservation.domain.payment.repository.PointHistoryRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ReservationRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import daewoo.team5.hotelreservation.global.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
    private final PointHistoryRepository pointHistoryRepository;
    private final UsersRepository usersRepository;

    private final Double POINT_RATE = 0.004; // 0.4% 적립
    private final ReservationRepository reservationRepository;

    public Long calculatePoints(Long finalAmount) {
        return Math.round(finalAmount * POINT_RATE);
    }

    public List<PointHistorySummaryProjection> getPointHistoryUser(Long userId){
        return pointHistoryRepository.findSummaryByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional
    public void earnPoint(Long userId, Long finalAmount,String orderId) {
        Reservation reservation = reservationRepository.findByOrderId(orderId).orElseThrow(() -> new ApiException(404, "예약 정보 없음", "해당 예약 정보가 존재하지 않습니다."));
        Users myInfo = usersRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Long points = calculatePoints(finalAmount);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expireAt = createdAt.plusYears(1);
        Long pointAfter = myInfo.getPoint() + points.intValue();
        pointHistoryRepository.save(
                PointHistoryEntity.builder()
                        .type(PointHistoryEntity.PointType.EARN)
                        .user(myInfo)
                        .createdAt(createdAt)
                        .expireAt(expireAt.toLocalDate())
                        .amount(points)
                        .description("결제 후 포인트 적립")
                        .reservation(reservation)
                        .balanceAfter(pointAfter)
                        .build()
        );
        myInfo.setPoint(pointAfter);
    }
}
