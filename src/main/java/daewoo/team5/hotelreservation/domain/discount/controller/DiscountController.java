package daewoo.team5.hotelreservation.domain.discount.controller;

import daewoo.team5.hotelreservation.domain.discount.dto.DiscountCreateDto;
import daewoo.team5.hotelreservation.domain.discount.dto.DiscountDetailDto;
import daewoo.team5.hotelreservation.domain.discount.dto.DiscountResponseDto;
import daewoo.team5.hotelreservation.domain.discount.service.DiscountService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/owner/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    @AuthUser
    public ApiResult<DiscountResponseDto> createDiscount(UserProjection user, @RequestBody DiscountCreateDto dto) {
        return ApiResult.created(discountService.createDiscount(user.getId(), dto), "할인 생성이 완료되었습니다.");
    }

    @GetMapping
    @AuthUser
    public ApiResult<List<DiscountResponseDto>> getDiscounts(UserProjection user) {
        return ApiResult.ok(discountService.getDiscounts(user.getId()), "할인 목록 조회가 완료되었습니다.");
    }
    @GetMapping("/{discountId}") // [!code ++]
    @AuthUser // [!code ++]
    public ApiResult<DiscountDetailDto> getDiscountDetail(UserProjection user, @PathVariable Long discountId) { // [!code ++]
        return ApiResult.ok(discountService.getDiscountDetail(user.getId(), discountId), "할인 상세 정보 조회가 완료되었습니다."); // [!code ++]
    } // [!code ++]
    @DeleteMapping("/{discountId}")
    @AuthUser
    public ApiResult<Void> deleteDiscount(UserProjection user, @PathVariable Long discountId) {
        discountService.deleteDiscount(user.getId(), discountId);
        return ApiResult.ok(null, "할인 삭제가 완료되었습니다.");
    }
}