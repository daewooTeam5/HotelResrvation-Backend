package daewoo.team5.hotelreservation.domain.shoppingcart.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.shoppingcart.service.ShoppingCartService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    @PatchMapping("/{cartId}")
    public ApiResult<Boolean> updateQuantity(
            @PathVariable Long cartId,
            @AuthenticationPrincipal Long userId,
            @RequestParam int quantity
    ) {
        return ApiResult.ok(
                shoppingCartService.updateQuantity(cartId, userId, quantity),
                "장바구니 수량 변경 성공"
        );
    }

    @PostMapping("/{roodId}")
    public ApiResult<Boolean> addToCart(
            @PathVariable Long roodId,
            @AuthenticationPrincipal Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        return ApiResult.ok(
                shoppingCartService.addToCart(roodId, userId, startDate, endDate, quantity),
                "장바구니 추가 성공"
        );
    }

    @DeleteMapping("/{cartId}")
    public ApiResult<Boolean> removeFromCart(
            @PathVariable Long cartId,
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResult.ok(
                shoppingCartService.removeFromCart(cartId, userId),
                "장바구니 삭제 성공"
        );
    }

    @GetMapping("")
    public ApiResult<Integer> getCartItemCount(
            Authentication authentication
    ) {
        Long userId = extractUserId(authentication);
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 필요", "로그인이 필요합니다.");
        }
        return ApiResult.ok(
                shoppingCartService.getCartItemCount(userId),
                "장바구니 아이템 수 조회 성공"
        );
    }

    private Long extractUserId(Authentication authentication) {
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserProjection) {
                userId = ((UserProjection) principal).getId();
            } else if (principal instanceof Long) {
                userId = (Long) principal;
            } else if (principal instanceof String) {
                String principalStr = (String) principal;
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> subMap = mapper.readValue(principalStr, new TypeReference<>() {});
                    userId = Long.valueOf(String.valueOf(subMap.get("id")));
                } catch (Exception e) {
                    try {
                        userId = Long.valueOf(principalStr);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return userId;
    }
}
