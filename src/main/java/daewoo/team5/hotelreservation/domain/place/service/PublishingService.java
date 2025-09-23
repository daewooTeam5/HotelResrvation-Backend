package daewoo.team5.hotelreservation.domain.place.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.entity.*;

import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class PublishingService {

    private final PlaceRepository placeRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // --------------------------
    // 숙소 등록
    // --------------------------
    public Places registerHotel(PublishingDTO dto) {

        // 1. Place 엔티티 생성
        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .status(Places.Status.PENDING)
                .isPublic(true)
                .build();

        Places savedPlace = placeRepository.save(place); // 먼저 저장

        // 2. 주소 등록
        List<PlaceAddress> addresses = dto.getAddressList().stream()
                .map(a -> PlaceAddress.builder()
                        .sido(a.getSido())
                        .sigungu(a.getSigungu())
                        .roadName(a.getRoadName())
                        .detailAddress(a.getDetailAddress())
                        .postalCode(a.getPostalCode())
                        .place(savedPlace)
                        .build())
                .toList();
        savedPlace.setAddresses(addresses);

        // 3. 객실 등록
        List<Room> rooms = dto.getRooms().stream()
                .map(r -> {
                    try {
                        return Room.builder()
                                .roomType(r.getRoomType())
                                .bedType(objectMapper.writeValueAsString(r.getBedType()))
                                .capacityPeople(r.getCapacityPeople())
                                .price(BigDecimal.valueOf(r.getPrice()))
                                .status(Room.Status.AVAILABLE)
                                .place(savedPlace)
                                .build();
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        savedPlace.setRooms(rooms);

        // 4. 이미지 등록
        List<ImageList> images = dto.getImages().stream()
                .map(url -> ImageList.builder()
                        .imageUrl(url)
                        .place(savedPlace)
                        .build())
                .toList();
        savedPlace.setImages(images);

        // 5. 모든 연관 엔티티 저장
        return placeRepository.save(savedPlace);
    }

    // --------------------------
    // 모든 숙소 조회
    // --------------------------
    public List<PublishingDTO> getAllHotels() {
        return placeRepository.findAll().stream()
                .map(p -> PublishingDTO.builder()
                        .hotelName(p.getName())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
