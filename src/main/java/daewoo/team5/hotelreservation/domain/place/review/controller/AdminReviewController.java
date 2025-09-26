package daewoo.team5.hotelreservation.domain.place.review.controller;

import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponseDto;
import daewoo.team5.hotelreservation.domain.place.review.service.ReviewService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/review")
@RequiredArgsConstructor
public class AdminReviewController {

    private final ReviewService reviewService;

    @GetMapping("")
    public ApiResult<List<ReviewResponseDto>> getReviews() {
        List<ReviewResponseDto> reviews = reviewService.getAllReviews();
        return ApiResult.ok(reviews, "리뷰 조회 성공");
    }
}
