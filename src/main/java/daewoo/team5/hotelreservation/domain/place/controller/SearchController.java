package daewoo.team5.hotelreservation.domain.place.controller;


import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.global.core.common.ApiResult;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/amenities")
public class SearchController {

    private final PlaceRepository placeRepository;


    // 모든 호텔(서비스 여부 상관없이 전부)
    @GetMapping("/all")
    public ApiResult<List<Places>> getAllHotels() {
        return ApiResult.ok(placeRepository.findAll());
    }

    // 특정 Amenity를 가진 호텔들
    @GetMapping("/{amenityId}")
    public ApiResult<List<Places>> getHotelsByAmenity(@PathVariable Long amenityId) {
        return ApiResult.ok(placeRepository.findByAmenityId(amenityId));
    }
}
