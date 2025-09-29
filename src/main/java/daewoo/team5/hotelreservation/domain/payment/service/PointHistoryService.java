package daewoo.team5.hotelreservation.domain.payment.service;

import daewoo.team5.hotelreservation.domain.payment.projection.PointProjection;
import daewoo.team5.hotelreservation.domain.payment.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    public List<PointProjection> getUserPoints(Long userId) {
        return pointHistoryRepository.findPointsByUserId(userId);
    }
}
