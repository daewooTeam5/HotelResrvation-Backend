package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.projection.AdminPlaceProjection;
import daewoo.team5.hotelreservation.domain.place.service.PlaceService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/places/admin")
@RequiredArgsConstructor
public class AdminPlaceController {
    private final PlaceService placeService;

    @GetMapping("")
    public ApiResult<Page<AdminPlaceProjection>> getAdminPlace(
            @RequestParam Integer start,
            @RequestParam(required = false) Long placeId,
            @RequestParam(required = false) String approvalStatus,
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String placeName
    ) {
        return ApiResult.ok(
                placeService.getAdminPlace(placeId, approvalStatus, ownerName, placeName, start),
                "관리자용 호텔 상세 정보 조회 성공!!"
        );
    }
}
