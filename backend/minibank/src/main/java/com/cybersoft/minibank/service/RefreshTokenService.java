package com.cybersoft.minibank.service;

import com.cybersoft.minibank.payload.request.RefreshRequest;

public interface RefreshTokenService {
    String createRefreshToken(String username);
    String refreshToken(RefreshRequest refreshToken);
}
