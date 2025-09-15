package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.PlaceDetailResponse;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.place.service.PlaceService;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("")
    public ApiResult<Page<PlaceItemInfomation>> searchPlacesWithFilters(
            @RequestParam Integer start,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String checkIn,
            @RequestParam(required = false) String checkOut,
            @RequestParam(required = false) Integer adults,
            @RequestParam(required = false) Integer children,
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) String placeCategory,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        int people = (adults != null ? adults : 0) + (children != null ? children : 0);
        int roomCount = rooms != null ? rooms : 1;

        if (placeCategory != null && placeCategory.trim().isEmpty()) {
            placeCategory = null;
        }

        return ApiResult.ok(
                placeService.AllSearchPlaces(
                        start, name, checkIn, checkOut, people, roomCount,
                        placeCategory, minRating, minPrice, maxPrice
                ),
                "호텔 조회 성공!!"
        );
    }

    @GetMapping("/{placeId}")
    public ApiResult<PlaceDetailResponse> getPlaceDetail(@PathVariable Long placeId) {
        return ApiResult.ok(
                placeService.getPlaceDetail(placeId),
                "숙소 상세 조회 성공"
        );
    }
}
