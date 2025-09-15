package daewoo.team5.hotelreservation.domain.place.controller.swagger;


import daewoo.team5.hotelreservation.domain.place.dto.TestDto;
import daewoo.team5.hotelreservation.domain.place.entity.MyTest;
import daewoo.team5.hotelreservation.global.model.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@Tag(name = "테스트 API", description = "테스트용 API를 모아놓은 컨트롤러")
public interface TestSwagger {

    @Operation(summary = "문자열 생성 테스트", description = "문자열 데이터를 생성하고 반환합니다.")
    @ApiResponse(responseCode = "201", description = "Created")
    ApiResult<Map<String,String>> testPost(TestDto dto, HttpServletRequest request);

    @Operation(summary = "MyTest 객체 조회", description = "MyTest 객체를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "OK")
    ApiResult<MyTest> test();
}
