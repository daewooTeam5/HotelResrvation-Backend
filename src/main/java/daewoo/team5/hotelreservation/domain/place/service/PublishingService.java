package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.entity.*;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.ServiceRepository;
import daewoo.team5.hotelreservation.domain.users.entity.Users;
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

    private final PlaceRepository repository;
    private final ServiceRepository serviceRepository;

    // --------------------------
    // 1. 숙소 등록
    // --------------------------
    public Places registerHotel(PublishingDTO dto) {

        // 1. 기본 숙소 엔티티 생성
        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .status(Places.Status.PENDING)
                .isPublic(true)
                .build();

        // 2. 주소 변환 및 연관 설정
        List<PlaceAddress> addresses = dto.getAddressList().stream()
                .map(a -> PlaceAddress.builder()
                        .sido(a.getSido())
                        .sigungu(a.getSigungu())
                        .roadName(a.getRoadName())
                        .detailAddress(a.getDetailAddress())
                        .postalCode(a.getPostalNumber())
                        .place(place)
                        .build())
                .toList();
        place.setAddresses(addresses);

        // 3. 객실 변환 및 연관 설정
        List<Room> rooms = dto.getRooms().stream()
                .map(r -> Room.builder()
                        .roomType(r.getRoomType())
                        .bedType(r.getBedType().toString())
                        .capacityPeople(r.getCapacityPeople())
                        .price(BigDecimal.valueOf(r.getPrice()))
                        .status(Room.Status.AVAILABLE)
                        .place(place)
                        .build())
                .toList();
        place.setRooms(rooms);

        // 4. 이미지 변환 및 연관 설정
        List<ImageList> images = dto.getImages().stream()
                .map(url -> ImageList.builder()
                        .imageUrl(url)
                        .place(place)
                        .build())
                .toList();
        place.setImages(images);

        // 5. 편의시설 변환 및 연관 설정
        List<daewoo.team5.hotelreservation.domain.place.entity.PlaceService> services = dto.getAmenities().stream()
                .map(name -> {
                    daewoo.team5.hotelreservation.domain.place.entity.Service s = serviceRepository.findByServiceNameAndPlace(name, place)
                            .orElseGet(() -> daewoo.team5.hotelreservation.domain.place.entity.Service.builder()
                                    .serviceName(name)
                                    .place(place)
                                    .build());
                    return daewoo.team5.hotelreservation.domain.place.entity.PlaceService.builder()
                            .service(s)
                            .place(place)
                            .build();
                })
                .toList();
        place.setServices(services); // ⚡ 필드명에 맞춰 setServices 사용

        // 6. DB 저장 (Cascade 덕분에 연관 엔티티 모두 저장)
        return repository.save(place);
    }


    public List<PublishingDTO> getAllHotels() {
        return repository.findAll().stream()
                .map(p -> PublishingDTO.builder()
                        .hotelName(p.getName())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
    }


}
