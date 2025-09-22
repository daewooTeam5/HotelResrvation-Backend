package daewoo.team5.hotelreservation.redis;

import daewoo.team5.hotelreservation.domain.place.entity.Places;
import daewoo.team5.hotelreservation.domain.place.entity.Region;
import daewoo.team5.hotelreservation.domain.place.repository.PlaceRepository;
import daewoo.team5.hotelreservation.domain.place.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AutoCompleteDataLoader implements CommandLineRunner {

    private final AutoCompleteService redisService;
    private final RegionRepository regionRepository;
    private final PlaceRepository placeRepository;

    @Override
    public void run(String... args) {
        List<Region> regions = regionRepository.findAll();
        for (Region r : regions) {
            if (r.getSido() != null) {
                redisService.saveKeyword(r.getSido());
            }
            if (r.getSigungu() != null) {
                redisService.saveKeyword(r.getSigungu());
            }
            if (r.getDong() != null) {
                redisService.saveKeyword(r.getDong());
            }
        }

        // 2) 숙소명 데이터
        List<Places> places = placeRepository.findAll();
        for (Places p : places) {
            redisService.saveKeyword(p.getName());
        }

        System.out.println("자동완성 초기 데이터 적재 완료 ✅");
    }
}
