package daewoo.team5.hotelreservation.domain.users.controller;

import daewoo.team5.hotelreservation.domain.place.review.dto.MyReviewResponseDTO;
import daewoo.team5.hotelreservation.domain.place.review.service.ReviewService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewSearchController {

    private final ReviewService reviewService;

    // GET 방식으로 userId를 경로 파라미터로 받아 내 리뷰 조회
    @GetMapping("/my")
    @AuthUser
    public ApiResult<List<MyReviewResponseDTO>> getMyReviews(UserProjection user) {
        List<MyReviewResponseDTO> reviews = reviewService.getReviewsByUser(user.getId());
        return ApiResult.ok(reviews, "내 리뷰 조회 성공");
    }
}