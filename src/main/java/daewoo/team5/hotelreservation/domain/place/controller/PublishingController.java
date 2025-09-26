package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.dto.SearchDTO;
import daewoo.team5.hotelreservation.domain.place.service.PublishingService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hotel/publishing")
@RequiredArgsConstructor
public class PublishingController {//api리설트는 컨트롤러를 바꿔주기
    //apiResult.created는 post처럼 내가 데이터를 받아오는 경우 사용 나머지는 ok로(조회)

    private final PublishingService publishingService;
    //String 타입 붙히면 ""로 내가 쓰고싶은 말 쓰고 호출

    // 숙소 등록
    @PostMapping("/register")
    public ApiResult<String> registerHotel(@RequestBody PublishingDTO publishingDTO) {
        publishingService.registerHotel(publishingDTO);
        return ApiResult.created(publishingDTO.getHotelName(),"숙소 등록 성공");
    }

    @GetMapping("/my-list")
    public ApiResult<List<SearchDTO>> getMyHotels(@RequestParam Long ownerId) {
        return ApiResult.ok(publishingService.getMyHotels(ownerId));
    }



    // 숙소 삭제
    @DeleteMapping("/list/{id}")
    public ApiResult<String> deleteHotel(@PathVariable Long id) {
        publishingService.deleteHotel(id);
        return ApiResult.ok("삭제 완료");
    }
}
