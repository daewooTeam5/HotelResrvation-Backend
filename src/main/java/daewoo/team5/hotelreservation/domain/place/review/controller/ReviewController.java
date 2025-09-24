// src/main/java/daewoo/team5/hotelreservation/domain/place/review/controller/ReviewController.java
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

@RestController
@RequestMapping("/api/v1/places/{placeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 주석: 특정 숙소의 리뷰 목록을 조회하는 API입니다. 정렬 파라미터를 받을 수 있습니다.
    @GetMapping
    public ApiResult<List<ReviewResponse>> getReviews(
            @PathVariable Long placeId,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sortBy) {
        return ApiResult.ok(reviewService.getReviewsByPlace(placeId, sortBy), "리뷰 조회 성공");
    }

    // 주석: 새로운 리뷰를 작성하는 API입니다.
    @PostMapping
    @AuthUser
    public ApiResult<ReviewResponse> createReview(
            @PathVariable Long placeId,
            @Valid @RequestBody CreateReviewRequest request,
            UserProjection user) {
        return ApiResult.created(reviewService.createReview(placeId, request, user), "리뷰 등록 성공");
    }

    // 주석: 리뷰를 삭제하는 API입니다.
    @DeleteMapping("/{reviewId}")
    @AuthUser
    public ApiResult<Void> deleteReview(
            @PathVariable Long placeId, // URL 경로의 일관성을 위해 유지
            @PathVariable Long reviewId,
            UserProjection user) {
        reviewService.deleteReview(reviewId, user);
        return ApiResult.ok(null, "리뷰가 성공적으로 삭제되었습니다.");
    }
}