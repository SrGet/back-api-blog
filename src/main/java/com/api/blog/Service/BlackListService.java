package com.api.blog.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addToBlackList(Jwt jwt){

        String jti = jwt.getId();
        long ttl = jwt.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();

        if(ttl > 0){
            redisTemplate.opsForValue().set(jti, "Blacklisted", ttl, TimeUnit.SECONDS);
        }
    }

    public Boolean isTokenBlackListed(String jti){
        return redisTemplate.hasKey(jti);
    }
}
