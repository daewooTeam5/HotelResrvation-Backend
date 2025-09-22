package daewoo.team5.hotelreservation.domain.wishlist.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.wishlist.repository.WishListRepository;
import daewoo.team5.hotelreservation.domain.wishlist.service.WishListService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/wishlist")
@RequiredArgsConstructor
public class WishListController {

    private final WishListService wishListService;
    private final WishListRepository wishListRepository;

    @PostMapping("/{placeId}")
    public ApiResult<Boolean> addWishList(@PathVariable Long placeId, @AuthenticationPrincipal Long userId){
        return ApiResult.ok(
                wishListService.addWishList(placeId, userId),
                "찜 등록 성공"
        );
    }

    @DeleteMapping("/{placeId}")
    public ApiResult<Boolean> removeWishList(@PathVariable Long placeId, @AuthenticationPrincipal Long userId){
        return ApiResult.ok(
                wishListService.removeWishList(placeId, userId),
                "찜 삭제 성공"
        );
    }

    @GetMapping("/{placeId}")
    public ApiResult<Boolean> isWishList(@PathVariable Long placeId, Authentication authentication){
        Long userId = extractUserId(authentication);
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 필요", "로그인이 필요합니다.");
        }
        return ApiResult.ok(
                wishListRepository.existsByUserIdAndPlaceId(userId, placeId),
                "찜 여부 확인 성공"
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
