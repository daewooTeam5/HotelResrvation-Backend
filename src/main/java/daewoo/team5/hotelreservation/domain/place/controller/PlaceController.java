package daewoo.team5.hotelreservation.domain.place.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.place.dto.PlaceDetailResponse;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.place.service.PlaceService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.wishlist.service.WishListService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;
    private final WishListService wishListService;

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
            @RequestParam(required = false) Double maxPrice,
            Authentication authentication
    )  {
        Long userId = extractUserId(authentication);
        int people = (adults != null ? adults : 0) + (children != null ? children : 0);
        int roomCount = rooms != null ? rooms : 1;

        if (placeCategory != null && placeCategory.trim().isEmpty()) {
            placeCategory = null;
        }

        return ApiResult.ok(
                placeService.AllSearchPlaces(
                        start, name, checkIn, checkOut, people, roomCount,
                        placeCategory, minRating, minPrice, maxPrice,
                        userId
                ),
                "호텔 조회 성공!!"
        );
    }



    @GetMapping("/{placeId}")
    public ApiResult<PlaceDetailResponse> getPlaceDetail(
            @PathVariable Long placeId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return ApiResult.ok(
                placeService.getPlaceDetail(placeId, startDate, endDate),
                "숙소 상세 조회 성공"
        );
    }

    @GetMapping("/wishlist")
    public ApiResult<Page<PlaceItemInfomation>> getUserWishList(
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
            @RequestParam(required = false) Double maxPrice,
            Authentication authentication
    ) {
        Long userId = extractUserId(authentication);
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 필요", "로그인이 필요합니다.");
        }

        int people = (adults != null ? adults : 0) + (children != null ? children : 0);
        int roomCount = rooms != null ? rooms : 1;

        if (placeCategory != null && placeCategory.trim().isEmpty()) {
            placeCategory = null;
        }

        return ApiResult.ok(
                wishListService.getUserWishList(
                        userId, name, checkIn, checkOut, people, roomCount,
                        placeCategory, minRating, minPrice, maxPrice, start
                ),
                "위시리스트 조회 성공"
        );


    }

    private Long extractUserId(Authentication authentication) {
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserProjection) {
                // Projection을 principal로 쓸 때
                userId = ((UserProjection) principal).getId();
            } else if (principal instanceof Long) {
                // JwtProvider에서 Long userId를 principal로 세팅했을 때
                userId = (Long) principal;
            } else if (principal instanceof String) {
                // principal이 JSON 문자열일 수 있는 경우 대비
                String principalStr = (String) principal;
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> subMap = mapper.readValue(principalStr, new TypeReference<>() {});
                    userId = Long.valueOf(String.valueOf(subMap.get("id")));
                } catch (Exception e) {
                    // 혹시 그냥 userId 문자열인 경우
                    try {
                        userId = Long.valueOf(principalStr);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return userId;
    }
}
