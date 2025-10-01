package daewoo.team5.hotelreservation.domain.users.controller;

import daewoo.team5.hotelreservation.domain.payment.projection.PointModificationRequest;
import daewoo.team5.hotelreservation.domain.users.dto.request.OwnerRequestDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserAllDataDTO;
import daewoo.team5.hotelreservation.domain.users.service.AdminUserService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/{userId}/all-data")
    public ResponseEntity<UserAllDataDTO> getAllUserData(@PathVariable Long userId) {
        UserAllDataDTO userData = adminUserService.getAllUserData(userId);
        return ResponseEntity.ok(userData);
    }

    @PatchMapping("/{userId}/status")
    public ApiResult<?> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UpdateStatusRequest request
    ) {
        adminUserService.updateUserStatus(userId, request.getStatus());
        return ApiResult.ok(null, "사용자 상태가 성공적으로 변경되었습니다.");
    }

    // 요청 본문 DTO
    @Data
    public static class UpdateStatusRequest {
        private String status; // "active", "inactive", "banned"
    }

    @GetMapping("/owner-requests")
    public ApiResult<List<OwnerRequestDto>> getOwnerRequests() {
        List<OwnerRequestDto> ownerRequests = adminUserService.getAllOwnerRequests();
        return ApiResult.ok(ownerRequests, "숙소 오너 요청 목록 조회가 완료되었습니다.");
    }

    @PatchMapping("/owner-requests/{requestId}/approve")
    public ApiResult<Void> approveOwnerRequest(@PathVariable Long requestId) {
        adminUserService.approveOwnerRequest(requestId);
        return ApiResult.ok(null, "오너 요청이 승인되었습니다.");
    }

    /**
     * 오너 요청 거절
     */
    @PatchMapping("/owner-requests/{requestId}/reject")
    public ApiResult<Void> rejectOwnerRequest(
            @PathVariable Long requestId,
            @RequestBody RejectRequest rejectRequest
    ) {
        adminUserService.rejectOwnerRequest(requestId, rejectRequest.getReason());
        return ApiResult.ok(null, "오너 요청이 거절되었습니다.");
    }

    public static class RejectRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    @PostMapping("/{userId}/points/add")
    public ApiResult<Void> addPointsToUser(
            @PathVariable Long userId,
            @RequestBody PointModificationRequest request
    ) {
        adminUserService.addPoints(userId, request.getAmount(), request.getReason());
        return ApiResult.ok(null, "포인트가 성공적으로 지급되었습니다.");
    }

    @PostMapping("/{userId}/points/deduct")
    public ApiResult<Void> deductPointsFromUser(
            @PathVariable Long userId,
            @RequestBody PointModificationRequest request
    ) {
        adminUserService.deductPoints(userId, request.getAmount(), request.getReason());
        return ApiResult.ok(null, "포인트가 성공적으로 차감되었습니다.");
    }
}