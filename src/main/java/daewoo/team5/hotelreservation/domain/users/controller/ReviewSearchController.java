package daewoo.team5.hotelreservation.domain.users.controller;

import daewoo.team5.hotelreservation.domain.place.review.dto.MyReviewResponse;
import daewoo.team5.hotelreservation.domain.place.review.dto.MyReviewResponseDTO;
import daewoo.team5.hotelreservation.domain.place.review.service.ReviewService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;


import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewSearchController {

    private final ReviewService reviewService;

    @GetMapping("/my-reviews")
    public ResponseEntity<Page<MyReviewResponse>> getMyReviews(
            @AuthenticationPrincipal Long userId, // 또는 커스텀 UserDetails 사용
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<MyReviewResponse> reviews = reviewService.getMyReviews(userId, page, size);
        return ResponseEntity.ok(reviews);
    }
}