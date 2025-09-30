package daewoo.team5.hotelreservation.domain.place.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.service.PublishingService;
import daewoo.team5.hotelreservation.domain.users.projection.UserProjection;
import daewoo.team5.hotelreservation.global.aop.annotation.AuthUser;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/hotel/publishing")
@RequiredArgsConstructor
public class PublishingController {//api리설트는 컨트롤러를 바꿔주기
    //apiResult.created는 post처럼 내가 데이터를 받아오는 경우 사용 나머지는 ok로(조회)

    private final PublishingService publishingService;
    private final ObjectMapper objectMapper;
    //String 타입 붙히면 ""로 내가 쓰고싶은 말 쓰고 호출

    // 숙소 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @AuthUser
    public ApiResult<Places> registerHotel(
            @RequestPart("data") String data,
            @RequestPart(value = "hotelImages", required = false) List<MultipartFile> hotelImages,
            MultipartHttpServletRequest multipartRequest,
            UserProjection user
    ) {
        PublishingDTO publishingDTO = parseJsonData(data);
        System.out.println(publishingDTO)   ;

        Map<Integer, List<MultipartFile>> roomImagesMap = extractRoomImagesMap(publishingDTO, multipartRequest);

        Places places = publishingService.registerHotel(user, publishingDTO, hotelImages, roomImagesMap);
        return ApiResult.created(places, "숙소 등록 성공");
    }

    //업데이트
    @PutMapping(value = "/update/{placeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<Long> updateHotel(
            @PathVariable Long placeId,
            @RequestPart("data") String data,
            @RequestPart(value = "hotelImages", required = false) List<MultipartFile> hotelImages,
            MultipartHttpServletRequest multipartRequest
    ) {
        PublishingDTO publishingDTO = parseJsonData(data);

        Map<Integer, List<MultipartFile>> roomImagesMap = extractRoomImagesMap(publishingDTO, multipartRequest);

        Places updatedPlace = publishingService.updateHotel(placeId, publishingDTO, hotelImages, roomImagesMap);
        return ApiResult.ok(updatedPlace.getId(), "숙소 정보가 성공적으로 수정되었습니다.");
    }


    @GetMapping("/get/{placeId}")
    public ApiResult<PublishingDTO> getHotel(@PathVariable Long placeId) {
        PublishingDTO hotelDetails = publishingService.getHotel(placeId);
        return ApiResult.ok(hotelDetails);
    }

    // 숙소 전체 조회
    @GetMapping("/my-list")
    @AuthUser
    public ApiResult<List<PublishingDTO>> getAllHotels(UserProjection user) {  //ApiResult<>이걸로 여기만 묶어주기
        return ApiResult.ok(publishingService.getAllHotels(user.getId()));
    }


    @DeleteMapping("/delete/{placeId}") // 💡 프론트엔드 호출 경로와 일치하는지 확인!
    public ApiResult<String> deleteHotel(@PathVariable Long placeId) {
        publishingService.deleteHotel(placeId);
        return ApiResult.ok("숙소가 성공적으로 삭제되었습니다.");
    }

    private PublishingDTO parseJsonData(String data) {
        try {
            return objectMapper.readValue(data, PublishingDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("요청 데이터 파싱 실패", e);
        }
    }

    private Map<Integer, List<MultipartFile>> extractRoomImagesMap(PublishingDTO dto, MultipartHttpServletRequest multipartRequest) {
        Map<Integer, List<MultipartFile>> map = new HashMap<>();
        if (dto == null || dto.getRooms() == null) return map;
        for (int i = 0; i < dto.getRooms().size(); i++) {
            List<MultipartFile> files = multipartRequest.getFiles("roomImages_" + i);
            if (!CollectionUtils.isEmpty(files)) {
                map.put(i, files);
            }
        }
        return map;
    }
}
