package daewoo.team5.hotelreservation.domain.statistics.service;

import daewoo.team5.hotelreservation.domain.statistics.dto.StatisticsResponse;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    public StatisticsResponse getReservationStats(String period, String start, String end, String type) {
        List<String> labels = Arrays.asList("1월", "2월", "3월", "4월", "5월", "6월");
        List<Number> values = Arrays.asList(120, 150, 180, 200, 170, 190);
        return new StatisticsResponse("예약/매출 통계", period, labels, values, Map.of("type", type));
    }

    public StatisticsResponse getCustomerStats(String period) {
        List<String> labels = Arrays.asList("신규", "재방문");
        List<Number> values = Arrays.asList(80, 20);
        return new StatisticsResponse("고객 통계", period, labels, values, null);
    }

    public StatisticsResponse getReviewStats(String period) {
        List<String> labels = Arrays.asList("1점", "2점", "3점", "4점", "5점");
        List<Number> values = Arrays.asList(2, 5, 10, 20, 50);
        return new StatisticsResponse("리뷰 평점 분포", period, labels, values, null);
    }

    public StatisticsResponse getRoomStats(String period) {
        List<String> labels = Arrays.asList("AVAILABLE", "RESERVED", "CLEANING");
        List<Number> values = Arrays.asList(30, 50, 10);
        return new StatisticsResponse("객실 상태 통계", period, labels, values, null);
    }
}