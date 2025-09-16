package daewoo.team5.hotelreservation.domain.auth.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class BlackListRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    // 로그아웃등 수행시 토큰 블랙리스트 추가
    public void addToBlackList(String token, long expirationInMillis) {
        String key = BLACKLIST_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted");
        redisTemplate.expire(key, expirationInMillis, TimeUnit.MILLISECONDS);
    }

    // 토큰이 블랙리스트에 있는지 확인
    public boolean isBlackListed(String token) {
        String key = BLACKLIST_KEY_PREFIX + token;
        return redisTemplate.hasKey(key);
    }

}
