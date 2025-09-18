package daewoo.team5.hotelreservation.domain.place.review.controller;

import daewoo.team5.hotelreservation.domain.place.review.dto.CreateReviewRequest;
import daewoo.team5.hotelreservation.domain.place.review.dto.ReviewResponse;
import daewoo.team5.hotelreservation.domain.place.review.service.ReviewService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 주석: 리뷰 관련 API 요청을 처리하는 컨트롤러입니다.
@RestController
@RequestMapping("/api/v1/places/{placeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 주석: 특정 숙소의 리뷰 목록을 조회하는 API입니다.
    @GetMapping
    public ApiResult<List<ReviewResponse>> getReviews(@PathVariable Long placeId) {
        return ApiResult.ok(reviewService.getReviewsByPlace(placeId), "리뷰 조회 성공");
    }

    // 주석: 새로운 리뷰를 작성하는 API입니다.
    @PostMapping
    @AuthUser // 현재 로그인한 사용자 정보를 주입받기 위한 어노테이션
    public ApiResult<ReviewResponse> createReview(
            @PathVariable Long placeId,
            @Valid @RequestBody CreateReviewRequest request,
            UserProjection user) { // @AuthUser 어노테이션에 의해 인증된 사용자 정보가 주입됩니다.
        return ApiResult.created(reviewService.createReview(placeId, request, user), "리뷰 등록 성공");
    }
}