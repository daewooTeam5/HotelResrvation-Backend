package daewoo.team5.hotelreservation.domain.users.controller;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserResponse;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.repository.UsersRepository;
import daewoo.team5.hotelreservation.domain.users.service.UsersService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UsersService usersService;
    private final UsersRepository usersRepository;

    @GetMapping("/users")
    public ApiResult<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Object principal = authentication.getPrincipal();
        System.out.println("principal = " + principal);
        System.out.println("principal class = " + principal.getClass().getName());

        if (principal instanceof Long userId) {
            Users user = usersRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(404, "존재하지 않는 유저", "유저가 존재하지 않습니다."));
            if (user.getStatus() != Users.Status.active) {
                throw new ApiException(403, "승인 필요", "관리자 승인이 필요합니다.");
            }
        }

        Page<UserResponse> responsePage = usersService.getAllUsers(start, size);
        return ApiResult.ok(responsePage, "전체 유저 조회 성공");
    }

    @PatchMapping("/users/{id}/allow")
    public ResponseEntity<Void> allowUser(@PathVariable Long id) {
        usersService.allowUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/cancel")
    public ResponseEntity<Void> cancelUser(@PathVariable Long id) {
        usersService.cancelUser(id);
        return ResponseEntity.ok().build();
    }


}
