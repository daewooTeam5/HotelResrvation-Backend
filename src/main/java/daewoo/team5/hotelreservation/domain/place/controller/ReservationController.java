package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.payment.projection.NonMemberReservationDetailProjection;
import daewoo.team5.hotelreservation.domain.place.dto.NonMemberReservationRequest;
import daewoo.team5.hotelreservation.domain.place.dto.ReservationDetailDTO;
import daewoo.team5.hotelreservation.domain.place.service.ReservationService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import daewoo.team5.hotelreservation.domain.place.dto.ReviewableReservationResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 주석: 현재 로그인한 사용자가 특정 숙소에 대해 리뷰를 작성할 수 있는지 확인하는 API
     * @param placeId 숙소 ID
     * @param user 현재 로그인한 사용자 정보 (@AuthUser 어노테이션으로 자동 주입)
     * @return 리뷰 작성 가능 여부를 담은 응답
     */
    @GetMapping("/can-review")
    @AuthUser
    public ApiResult<Map<String, Boolean>> canReview(@RequestParam Long placeId, UserProjection user) {
        boolean canReview = reservationService.canUserWriteReview(placeId, user);
        return ApiResult.ok(Map.of("canReview", canReview));
    }

    /**
     * ✅ [추가] 현재 로그인한 사용자가 특정 숙소에 대해 리뷰를 작성할 수 있는 예약 목록을 조회하는 API
     * @param placeId 숙소 ID
     * @param user 현재 로그인한 사용자 정보
     * @return 리뷰 작성 가능한 예약 목록
     */
    @GetMapping("/reviewable")
    @AuthUser
    public ApiResult<List<ReviewableReservationResponse>> getReviewableReservations(@RequestParam Long placeId, UserProjection user) {
        List<ReviewableReservationResponse> reservations = reservationService.getReviewableReservations(placeId, user);
        return ApiResult.ok(reservations);
    }

    /**
     * 비회원 예약 조회
     * @param request
     * @return
     */
    @PostMapping("/non-member")
    public ApiResult<NonMemberReservationDetailProjection> getNonMemberReservation(@RequestBody NonMemberReservationRequest request) {
        return ApiResult.ok(reservationService.getNonMemberReservation(request));
    }
}