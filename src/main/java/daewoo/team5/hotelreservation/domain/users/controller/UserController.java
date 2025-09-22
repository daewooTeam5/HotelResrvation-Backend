package daewoo.team5.hotelreservation.domain.users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.shoppingcart.projection.CartProjection;
import daewoo.team5.hotelreservation.domain.shoppingcart.service.ShoppingCartService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ShoppingCartService shoppingCartService;

    @GetMapping("/cart")
    public ApiResult<List<CartProjection>> getCartItems(Authentication authentication) {
        Long userId = extractUserId(authentication);
        if (userId == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "인증 필요", "로그인이 필요합니다.");
        }
        return ApiResult.ok(shoppingCartService.getCartItems(userId), "장바구니 아이템 조회 성공");
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
