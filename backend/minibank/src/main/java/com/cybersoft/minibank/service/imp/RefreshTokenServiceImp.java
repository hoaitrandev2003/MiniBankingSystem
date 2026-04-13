package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.RefreshTokenEntity;
import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.exception.InvalidUserException;
import com.cybersoft.minibank.repository.RefreshTokenRepository;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImp implements RefreshTokenService {
    private final int refreshTokenDurationMs = 360000;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String createRefreshToken(String username) {
        UserEntity user = userRepository.findByUserName(username).orElseThrow(InvalidUserException::new);

        // Yêu cầu: Mỗi người dùng chỉ có 1 token đang hoạt động -> Xóa cái cũ trước
        refreshTokenRepository.deleteByUser(user);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Timestamp.from(Instant.now().plusMillis(refreshTokenDurationMs)).toLocalDateTime());
        refreshToken.setToken(UUID.randomUUID().toString()); // Tạo chuỗi ngẫu nhiên duy nhất

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }


}
