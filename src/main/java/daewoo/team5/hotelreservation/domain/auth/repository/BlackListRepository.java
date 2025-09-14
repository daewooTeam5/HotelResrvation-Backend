package daewoo.team5.hotelreservation.domain.auth.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BlackListRepository {
    private final RedisTemplate<String, Object> redisTemplate;

}
