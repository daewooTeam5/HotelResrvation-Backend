package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.service.PublishingService;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
//업데이트
    @PutMapping("/update/{placeId}") // 💡 PUT 매핑과 @PathVariable 사용
    public ApiResult<Long> updateHotel(
            @PathVariable Long placeId,
            @RequestBody PublishingDTO publishingDTO
    ) {
        Places updatedPlace = publishingService.updateHotel(placeId, publishingDTO);
        return ApiResult.ok(updatedPlace.getId(), "숙소 정보가 성공적으로 수정되었습니다.");
    }

    // 숙소 전체 조회
    @GetMapping("/my-list")
    public ApiResult<List<PublishingDTO>> getAllHotels(@RequestParam Long ownerId) {  //ApiResult<>이걸로 여기만 묶어주기
        return ApiResult.ok(publishingService.getAllHotels(ownerId));
    }

    // 특정 숙소 조회(지금은 더미데이터)
    @GetMapping("/list/{id}")
    public ApiResult<PublishingDTO> getHotel(@PathVariable Long id) {
        return ApiResult.ok(publishingService.getHotel(id)) ;
    }

}
