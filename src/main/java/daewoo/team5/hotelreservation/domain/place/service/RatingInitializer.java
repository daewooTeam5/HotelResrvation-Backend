package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.review.entity.Review;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RatingInitializer implements CommandLineRunner {

    private final PlaceRepository placeRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("서버 시작 시 숙소별 평균 평점 계산을 시작합니다...");

        List<Places> places = placeRepository.findAll();

        for (Places place : places) {
            List<Review> reviews = reviewRepository.findByPlaceId(place.getId(), null);
            if (reviews.isEmpty()) {
                place.setAvgRating(BigDecimal.ZERO);
                place.setReviewCount(0);
            } else {
                double average = reviews.stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);
                place.setAvgRating(BigDecimal.valueOf(average).setScale(2, RoundingMode.HALF_UP));
                place.setReviewCount(reviews.size());
            }
            placeRepository.save(place);
        }
        log.info("숙소별 평균 평점 계산 및 업데이트가 완료되었습니다.");
    }
}