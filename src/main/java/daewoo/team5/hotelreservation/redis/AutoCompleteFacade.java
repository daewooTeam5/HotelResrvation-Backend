package daewoo.team5.hotelreservation.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutoCompleteFacade {
    private final AutoCompleteService redisService;
    private final AutoCompleteRepository repository;

    public AutoCompleteResponse getSuggestions(String prefix) {
        // DB 조회
        List<String> regions = repository.findRegions(prefix);
        List<String> places = repository.findPlaces(prefix);

        // Redis 캐시에 저장 (optional)
        regions.forEach(redisService::saveKeyword);
        places.forEach(redisService::saveKeyword);

        // DTO 반환
        return new AutoCompleteResponse(regions, places);
    }
}
