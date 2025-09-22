package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.PlaceDetailResponse;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceDetailProjection;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceServiceProjection;
import daewoo.team5.hotelreservation.domain.place.projection.RoomInfo;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.global.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public Page<PlaceItemInfomation> AllSearchPlaces(
            int start, String name, String checkIn, String checkOut,
            int people, int rooms,
            String placeCategory, Double minRating, Double minPrice, Double maxPrice,
            Long userId
    ) {
        return placeRepository.findAllSearchPlaceInfo(
                name, checkIn, checkOut, people, rooms,
                placeCategory, minRating, minPrice, maxPrice,
                userId,
                PageRequest.of(start - 1, 10)
        );
    }


    public PlaceDetailResponse getPlaceDetail(Long placeId, LocalDate startDate, LocalDate endDate) {
        PlaceDetailProjection detail = placeRepository.findPlaceDetail(placeId).orElseThrow(() -> new ApiException(HttpStatus.MULTI_STATUS,"에러","에러"));
        List<String> images = placeRepository.findPlaceImages(placeId);
        List<RoomInfo> rooms = placeRepository.findRoomsByPlace(placeId, startDate, endDate);
        List<PlaceServiceProjection> services = placeRepository.findPlaceServices(placeId);
        return new PlaceDetailResponse(detail, images, rooms, services);
    }

}

