package daewoo.team5.hotelreservation.domain.users.controller;
import daewoo.team5.hotelreservation.domain.users.dto.request.UserResponse;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
import daewoo.team5.hotelreservation.domain.users.service.UsersService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UsersService usersService;

    @GetMapping("/users")
    public ApiResult<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size) {

        Page<UserResponse> responsePage = usersService.getAllUsers(start, size);
        return ApiResult.ok(responsePage, "전체 유저 조회 성공");
    }
}
