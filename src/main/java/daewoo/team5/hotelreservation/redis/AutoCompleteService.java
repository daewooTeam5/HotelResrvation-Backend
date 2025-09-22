package daewoo.team5.hotelreservation.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AutoCompleteService {
    private final StringRedisTemplate redisTemplate;

    public void saveKeyword(String keyword) {
        String key = "autocomplete";
        redisTemplate.opsForZSet().add(key, keyword, 0); // 점수는 정렬용, 여기선 0
    }

    public List<String> getSuggestions(String prefix) {
        String key = "autocomplete";
        // Redis에서 prefix로 시작하는 값 찾기
        Set<String> results = redisTemplate.opsForZSet()
                .rangeByLex(key, Range.rightOpen(prefix, prefix + Character.MAX_VALUE));
        return results != null ? new ArrayList<>(results) : Collections.emptyList();
    }
}
