package daewoo.team5.hotelreservation.domain.place.review.controller;

import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponseDto;
import daewoo.team5.hotelreservation.domain.place.review.repository.ReviewRepository;
import daewoo.team5.hotelreservation.domain.place.review.service.ReviewService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/review")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewRepository reviewRepository;

    @GetMapping("")
    public ApiResult<List<ReviewResponseDto>> getReviews(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String placeName,
            @RequestParam(required = false) String replyStatus) {

        List<ReviewResponseDto> reviews;
        if (userName == null && placeName == null && replyStatus == null) {
            reviews = reviewRepository.findAllReviewsWithDetails();
        } else {
            reviews = reviewRepository.searchReviews(userName, placeName, replyStatus);
        }
        return ApiResult.ok(reviews, "리뷰 조회 성공");
    }

    @DeleteMapping("/{reviewId}")
    public ApiResult<Void> deleteReview(@PathVariable Long reviewId) {
        reviewRepository.deleteById(reviewId);
        return ApiResult.ok(null, "리뷰가 성공적으로 삭제되었습니다.");
    }
}
