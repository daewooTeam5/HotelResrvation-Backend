package daewoo.team5.hotelreservation.domain.place.controller;

import daewoo.team5.hotelreservation.domain.place.entity.Amenity;
import daewoo.team5.hotelreservation.domain.place.repository.AmentiesRepository;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/amenities")
public class AmenityController {
    private final AmentiesRepository amentiesRepository;

    @GetMapping("/place")
    public ApiResult<List<Amenity>> getAllPlacesAmenities() {
        return ApiResult.ok(amentiesRepository.findAllByType(Amenity.Type.PLACE), "편의시설 목록 조회 성공");
    }

    @GetMapping("/room")
    public ApiResult<List<Amenity>> getAllRoomsAmenities() {
        return ApiResult.ok(amentiesRepository.findAllByType(Amenity.Type.ROOM), "편의시설 목록 조회 성공");
    }
}
