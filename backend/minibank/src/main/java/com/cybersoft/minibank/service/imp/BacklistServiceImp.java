package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.service.BacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BacklistServiceImp implements BacklistService {

    private final RedisTemplate<String,String> redisTemplate;

    public void blacklistToken(String token, long minutes) {

        redisTemplate.opsForValue().set(
                "BLACKLIST:" + token,
                "REVOKED",
                Duration.ofMinutes(minutes)
        );
    }

    public boolean isBlacklisted(String token) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(
                        "BLACKLIST:" + token
                )
        );
    }
}
