package daewoo.team5.hotelreservation.domain.users.controller.swagger;

import daewoo.team5.hotelreservation.global.model.ApiResult;

import java.util.Map;

public interface UsersSwagger {
    ApiResult<String> createUser();
    ApiResult<Map<String,String>> login();
}
