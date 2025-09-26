package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.AdminPlaceProjection;
import daewoo.team5.hotelreservation.domain.place.service.PlaceService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/places/admin")
@RequiredArgsConstructor
public class AdminPlaceController {
    private final PlaceService placeService;

    @GetMapping("")
    public ApiResult<Page<AdminPlaceProjection>> getAdminPlace(
            @RequestParam Integer start,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String sigungu,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String placeName
    ) {
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

}
