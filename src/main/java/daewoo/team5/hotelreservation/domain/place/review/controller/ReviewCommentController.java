// src/main/java/daewoo/team5/hotelreservation/domain/place/review/controller/ReviewCommentController.java
package daewoo.team5.hotelreservation.domain.place.review.controller;

import daewoo.team5.hotelreservation.domain.place.review.dto.CreateReviewCommentRequest;
import daewoo.team5.hotelreservation.domain.place.review.service.ReviewCommentService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owner/reviews/{reviewId}/comments") // 경로를 관리자용으로 명확히 함
@RequiredArgsConstructor
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    /**
     * 주석: 특정 리뷰에 대한 관리자 댓글을 생성하는 API입니다.
     * @param reviewId 댓글을 달 리뷰의 ID
     * @param request 댓글 내용
     * @param user 현재 로그인한 관리자 정보
     * @return 성공 결과
     */
    @PostMapping
    @AuthUser
    public ApiResult<Void> createComment(
            @PathVariable Long reviewId,
            @Valid @RequestBody CreateReviewCommentRequest request,
            UserProjection user) {
        reviewCommentService.createComment(reviewId, request, user);
        return ApiResult.created(null, "관리자 댓글이 성공적으로 등록되었습니다.");
    }
}