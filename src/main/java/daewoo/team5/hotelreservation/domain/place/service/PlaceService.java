package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.PlaceDetailResponse;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceDetailProjection;
import daewoo.team5.hotelreservation.domain.place.projection.PlaceItemInfomation;
import daewoo.team5.hotelreservation.domain.place.projection.RoomInfo;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public Page<PlaceItemInfomation> AllSearchPlaces(
            int start, String name, String checkIn, String checkOut,
            int people, int rooms,
            String placeCategory, Double minRating, Double minPrice, Double maxPrice
    ) {
        return placeRepository.findAllSearchPlaceInfoTest(
                name, checkIn, checkOut, people, rooms,
                placeCategory, minRating, minPrice, maxPrice,
                PageRequest.of(start - 1, 10)
        );
    }

    public PlaceDetailResponse getPlaceDetail(Long placeId) {
        PlaceDetailProjection detail = placeRepository.findPlaceDetail(placeId);
        List<String> images = placeRepository.findPlaceImages(placeId);
        List<RoomInfo> rooms = placeRepository.findRoomsByPlace(placeId);

        return new PlaceDetailResponse(detail, images, rooms);
    }

}

