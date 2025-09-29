package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.AdminPlaceProjection;
import daewoo.team5.hotelreservation.domain.place.service.PlaceDetailService;
import daewoo.team5.hotelreservation.domain.place.service.PlaceService;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/places")
@RequiredArgsConstructor
public class AdminPlaceController {
    private final PlaceService placeService;
    private final UsersRepository usersRepository;
    private final PlaceDetailService placeDetailService;

    @GetMapping("")
    public ApiResult<Page<AdminPlaceProjection>> getAdminPlace(
            @RequestParam Integer start,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String sigungu,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String placeName,
            Authentication authentication
    ) {
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);
        System.out.println("principal class = " + principal.getClass().getName());
        if (principal instanceof Long userId) {
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(404, "존재하지 않는 유저", "유저가 존재하지 않습니다."));
            if (user.getStatus() != Users.Status.active) {
                throw new ApiException(403, "승인 필요", "관리자 승인이 필요합니다.");
            }
        }

        return ApiResult.ok(
                placeService.getAdminPlace(sido, sigungu, approvalStatus, ownerName, placeName, start),
                "관리자용 호텔 상세 정보 조회 성공!!"
        );
    }

    @PatchMapping("/{placeId}/approve")
    public ApiResult<?> approvePlace(@PathVariable Long placeId) {
        placeService.updatePlaceStatus(placeId, Places.Status.APPROVED);
        return ApiResult.ok(null, "숙소 승인 성공!!");
    }

    @PatchMapping("/{placeId}/reject")
    public ApiResult<?> rejectPlace(@PathVariable Long placeId) {
        placeService.updatePlaceStatus(placeId, Places.Status.REJECTED);
        return ApiResult.ok(null, "숙소 거절 성공!!");
    }

    @PatchMapping("/{placeId}/pending")
    public ApiResult<?> pendingPlace(@PathVariable Long placeId) {
        placeService.updatePlaceStatus(placeId, Places.Status.PENDING);
        return ApiResult.ok(null, "숙소 승인 성공!!");
    }

    @PatchMapping("/{placeId}/inactive")
    public ApiResult<?> inactivePlace(@PathVariable Long placeId) {
        placeService.updatePlaceStatus(placeId, Places.Status.INACTIVE);
        return ApiResult.ok(null, "숙소 거절 성공!!");
    }

    @GetMapping("/{placeId}")
    public ApiResult<?> getDetail(@PathVariable Long placeId) {
        return ApiResult.ok(placeDetailService.getPlaceDetail(placeId), "숙소 상세 조회 성공");
    }
}

