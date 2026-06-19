package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImp implements RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void setLock(String key, long timeoutMinutes) {
        redisTemplate.opsForValue().set(key, "LOCK", timeoutMinutes, TimeUnit.MINUTES);
    }

    @Override
    public boolean isLocked(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void save(String key, String value, long timeoutMinutes) {
        redisTemplate.opsForValue().set(key, value, timeoutMinutes, TimeUnit.MINUTES);
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }
}
