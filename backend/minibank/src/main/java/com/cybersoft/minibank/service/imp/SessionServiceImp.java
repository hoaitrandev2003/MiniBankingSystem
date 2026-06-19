package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.UserSessionDTO;
import com.cybersoft.minibank.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SessionServiceImp implements SessionService {
    private final RedisTemplate<String,String> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveSession(String username, UserSessionDTO session) {
        try {
            String json = objectMapper.writeValueAsString(session);
            redisTemplate.opsForValue().set(
                    "SESSION:" + username,
                    json,
                    Duration.ofDays(7)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserSessionDTO getSession(String username) {
        try {

            String json = redisTemplate.opsForValue().get("SESSION:" + username);

            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, UserSessionDTO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSession(String username) {
        redisTemplate.delete("SESSION:" + username);
    }
}
