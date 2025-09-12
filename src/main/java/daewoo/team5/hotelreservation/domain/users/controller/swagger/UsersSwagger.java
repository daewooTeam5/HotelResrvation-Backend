package daewoo.team5.hotelreservation.domain.users.controller.swagger;

import daewoo.team5.hotelreservation.domain.users.dto.request.CreateUserDto;
import daewoo.team5.hotelreservation.domain.users.dto.request.LogInUserDto;
import daewoo.team5.hotelreservation.domain.users.entity.UsersLegacy;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface UsersSwagger {
    ApiResult<UsersLegacy> createUser(CreateUserDto createUserDto);
    ApiResult<Map<String,String>> login(LogInUserDto logInUserDto);
    ApiResult<Page<UserProjection>> getAllUserPage(int start, int size);
}
