package daewoo.team5.hotelreservation.domain.place.service;

import daewoo.team5.hotelreservation.domain.place.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
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
    public Places registerHotel(PublishingDTO dto) {
        Places place = Places.builder()
                .name(dto.getHotelName())
                .description(dto.getDescription())
                .build();

        // rooms, addresses, amenities는 나중에 추가 가능
        return repository.save(place);
    }

    // 전체 조회
    public List<PublishingDTO> getAllHotels() {
        return repository.findAll().stream()
                .map(p -> PublishingDTO.builder()
                        .hotelName(p.getName())
                        .description(p.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    // 숙소 하나 조회
    public PublishingDTO getHotel(Long id) {
    Places place  = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("해당 숙소 없음"));
        return new PublishingDTO(//dto 모든 내용


        );
    }
}
