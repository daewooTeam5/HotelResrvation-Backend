package daewoo.team5.hotelreservation.domain.users.controller;

import daewoo.team5.hotelreservation.domain.users.controller.swagger.UsersSwagger;
import daewoo.team5.hotelreservation.domain.users.dto.request.CreateUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.LogInUserDto;
import daewoo.team5.hotelreservation.domain.users.entity.UsersLegacy;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.domain.users.service.UsersService;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Slf4j
public class UsersController implements UsersSwagger {
    private final UsersService usersService;

    @PostMapping
    @Override
    public ApiResult<UsersLegacy> createUser(@RequestBody CreateUserDto createUserDto) {
        UsersLegacy users = usersService.registerUser(createUserDto);
        return ApiResult.created(users,"회원가입 성공");
    }

    @PostMapping("/login")
    @Override
    public ApiResult<Map<String, String>> login(@RequestBody LogInUserDto logInUserDto) {
        return ApiResult.ok(
                usersService.login(logInUserDto)
                ,"로그인 성공"
        );
    }

    @GetMapping("/all")
    @Override
    public ApiResult<Page<UserProjection>> getAllUserPage(@RequestParam int start, @RequestParam int size) {
        return ApiResult.ok(
                usersService.getAllUserPage(start, size)
                ,"회원 목록 조회 성공"
        );
    }

    @GetMapping("/my")
    @AuthUser
    public ApiResult<UsersLegacy> getMyInfo(UsersLegacy user) {
        return ApiResult.ok(user, "내 정보 조회 성공");
    }
}
