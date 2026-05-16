package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.entity.RefreshTokenEntity;
import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.exception.InvalidRefreshTokenException;
import com.cybersoft.minibank.exception.InvalidUserException;
import com.cybersoft.minibank.mapper.UserMapper;
import com.cybersoft.minibank.payload.request.RefreshRequest;
import com.cybersoft.minibank.repository.RefreshTokenRepository;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.RefreshTokenService;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

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

    @Autowired
    private JwtUtilHelper jwtTokenUtil;

    @Override
    @Transactional
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

    @Override
    public String refreshToken(RefreshRequest refreshToken) {
        String tokenRefreshToken = refreshToken.getRefreshToken();

        RefreshTokenEntity token = refreshTokenRepository
                .findByToken(tokenRefreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        UserEntity user = token.getUser();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserDTO userDTO = UserMapper.mapDTO(user);
            String data = objectMapper.writeValueAsString(userDTO);

            return jwtTokenUtil.generateToken(data);
        } catch (Exception e) {
            throw new InvalidRefreshTokenException("Error generating token");
        }
    }
}
