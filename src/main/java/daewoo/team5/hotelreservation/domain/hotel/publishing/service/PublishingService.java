package daewoo.team5.hotelreservation.domain.hotel.publishing.service;

import daewoo.team5.hotelreservation.domain.hotel.publishing.dto.PublishingDTO;
import daewoo.team5.hotelreservation.domain.hotel.publishing.entity.Publishing;
import daewoo.team5.hotelreservation.domain.hotel.publishing.repository.PublishingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PublishingService {//리콰이어드가 있으면 AUTOWIRED가 없어도 됨

    private final PublishingRepository repository;

    // 등록
    public Publishing registerHotel(PublishingDTO dto) {
        Publishing publishing = Publishing.builder()
                .hotelName(dto.getHotelName())
                .introduction(dto.getDescription())
                .build();

        // rooms, addresses, amenities는 나중에 추가 가능
        return repository.save(publishing);
    }

    // 전체 조회
    public List<PublishingDTO> getAllHotels() {
        return repository.findAll().stream()
                .map(p -> PublishingDTO.builder()
                        .hotelName(p.getHotelName())
                        .description(p.getIntroduction())
                        .build())
                .collect(Collectors.toList());
    }

    // 숙소 하나 조회
    public PublishingDTO getHotel(Long id) {
    Publishing publishing = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("해당 숙소 없음"));

        return new PublishingDTO(//dto 모든 내용
                publishing.getHotelName(),
                publishing.getAddresses(),
                publishing.getImages(),
                publishing.getRooms(),
                publishing.getIntroduction(),
                publishing.getAmenities()
        );
    }
}
