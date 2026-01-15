package com.api.blog.Service;

import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtService jwtService;

    public void addToBlackList(HttpServletRequest request){
        String token = jwtService.getTokenFromRequest(request);

        String jti = jwtService.getJti(token);

        long ttl = jwtService.getExpiration(token).toInstant().getEpochSecond() - Instant.now().getEpochSecond();

        if(ttl > 0){
            redisTemplate.opsForValue().set(jti, "Blacklisted", ttl, TimeUnit.SECONDS);
        }
    }

    public Boolean isTokenBlackListed(String jti){
        return redisTemplate.hasKey(jti);
    }
}
