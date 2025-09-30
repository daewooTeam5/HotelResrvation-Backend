package daewoo.team5.hotelreservation.domain.users.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.coupon.projection.UserCouponProjection;
import daewoo.team5.hotelreservation.domain.coupon.service.CouponService;
import daewoo.team5.hotelreservation.domain.payment.entity.GuestEntity;
import daewoo.team5.hotelreservation.domain.payment.entity.PointHistoryEntity;
import daewoo.team5.hotelreservation.domain.payment.projection.PaymentDetailProjection;
import daewoo.team5.hotelreservation.domain.payment.projection.PointHistorySummaryProjection;
import daewoo.team5.hotelreservation.domain.payment.service.PaymentService;
import daewoo.team5.hotelreservation.domain.payment.service.PointService;
import daewoo.team5.hotelreservation.domain.place.repository.projection.PaymentSummaryProjection;
import daewoo.team5.hotelreservation.domain.shoppingcart.projection.CartProjection;
import daewoo.team5.hotelreservation.domain.shoppingcart.service.ShoppingCartService;
import daewoo.team5.hotelreservation.domain.users.dto.OwnerRequestDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserUpdateDTO;
import daewoo.team5.hotelreservation.domain.users.entity.OwnerRequestEntity;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.projection.MyInfoProjection;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.service.UsersService;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final ShoppingCartService shoppingCartService;
    private final CouponService couponService;
    private final PaymentService paymentService;
    private final UsersService usersService;
    private final PointService pointService;

    @GetMapping("/my/hotel-owner/status")
    @AuthUser
    public ApiResult<OwnerRequestEntity> getHotelOwnerStatus(UserProjection user){
        return ApiResult.ok(usersService.getHotelOwnerStatus(user.getId()), "호텔 운영자 상태 조회 성공");
    }


    @PostMapping(value = "/my/hotel-owner/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthUser
    public ApiResult<OwnerRequestEntity> requestHotelOwner(
            @ModelAttribute OwnerRequestDto requestDto, // JSON 데이터 대신 DTO로 바로 받기!
            @RequestParam("documents") List<MultipartFile> documents, // 'documents' 키로 넘어온 파일들을 리스트로 받기!
            UserProjection user
    ) {
        // 데이터가 잘 들어왔는지 로그로 확인해보자!
        System.out.println("DTO Data: " + requestDto.toString());
        documents.forEach(file -> {
            System.out.println("Uploaded File Name: " + file.getOriginalFilename());
            System.out.println("Uploaded File Size: " + file.getSize());
        });

        OwnerRequestEntity ownerRequest = usersService.createOwnerRequest(user.getId(), requestDto, documents);
        return ApiResult.ok(ownerRequest, "호텔 운영자 요청이 성공적으로 접수되었습니다.");
    }

    @GetMapping("/my/payments")
    @AuthUser
    public ApiResult<Page<PaymentSummaryProjection>> getMyPayments(
            UserProjection user,
            @RequestParam(defaultValue = "1") int page
    ) {
        return ApiResult.ok(paymentService.getPaymentsByUser(user, page - 1), "사용자 결제 내역 조회 성공");
    }

    @GetMapping("/my/payments/{id}")
    @AuthUser
    public ApiResult<PaymentDetailProjection> getMyPaymentDetail(
            @PathVariable(name = "id") Long paymentId,
            UserProjection user
    ) {
        return ApiResult.ok(paymentService.getPaymentDetail(paymentId, user.getId()), "사용자 결제 상세 내역 조회 성공");
    }

    @GetMapping("/my")
    @AuthUser
    public ApiResult<MyInfoProjection> getMyInfo(
            UserProjection user
    ) {
        return ApiResult.ok(usersService.getUserById(user.getId()), "사용자 정보 조회 성공");
    }
    @PutMapping("/update")
    @AuthUser
    public ApiResult<Users> updateMyProfile(
            UserProjection user,
            @RequestBody UserUpdateDTO dto
    ) {
        Users updated = usersService.updateUser(user.getId(), dto);
        return ApiResult.ok(updated, "프로필 업데이트 성공");
    }

    @GetMapping("/my/point-history")
    @AuthUser
    public ApiResult<List<PointHistorySummaryProjection>> getMyPointHistory(
            UserProjection user
    ) {
        return ApiResult.ok(pointService.getPointHistoryUser(user.getId()), "사용자 포인트 내역 조회 성공");
    }

    @GetMapping("/my/guest")
    @AuthUser
    public ApiResult<GuestEntity> getMyGuestInfo(
            UserProjection user
    ) {
        return ApiResult.ok(usersService.getGuestByUser(user), "사용자 게스트 정보 조회 성공");
    }

    @GetMapping("/my/coupons")
    @AuthUser
    public ApiResult<List<UserCouponProjection>> getUserCoupons(
            @RequestParam(defaultValue = "all") String type,
            UserProjection user) {
        return ApiResult.ok(couponService.getUserCoupons(user.getId(), type), "사용자 쿠폰 조회 성공");

    }

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
                    Map<String, Object> subMap = mapper.readValue(principalStr, new TypeReference<>() {
                    });
                    userId = Long.valueOf(String.valueOf(subMap.get("id")));
                } catch (Exception e) {
                    try {
                        userId = Long.valueOf(principalStr);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return userId;
    }
}
