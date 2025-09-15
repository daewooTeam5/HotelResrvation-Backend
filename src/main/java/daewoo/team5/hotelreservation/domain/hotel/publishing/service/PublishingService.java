package daewoo.team5.hotelreservation.domain.hotel.publishing.service;

import daewoo.team5.hotelreservation.domain.hotel.publishing.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Place;
import daewoo.team5.hotelreservation.domain.hotel.publishing.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PublishingService {//리콰이어드가 있으면 AUTOWIRED가 없어도 됨

    private final PlaceRepository repository;

    // 등록
    public Place registerHotel(PublishingDTO dto) {
        Place place = Place.builder()
                .hotelName(dto.getHotelName())
                .description(dto.getDescription())
                .build();

        // rooms, addresses, amenities는 나중에 추가 가능
        return repository.save(place);
    }

    // 전체 조회
    public List<PublishingDTO> getAllHotels() {
        return repository.findAll().stream()
                .map(p -> PublishingDTO.builder()
                        .hotelName(p.getHotelName())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    // 숙소 하나 조회
    public PublishingDTO getHotel(Long id) {
    Place place  = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("해당 숙소 없음"));

        return new PublishingDTO(//dto 모든 내용


        );
    }
}
