package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.LogInDTO;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.request.RegisterRequest;
import com.cybersoft.minibank.payload.request.VerifyRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationServices {
    LogInDTO login(LoginRequest loginRequest, HttpServletRequest request);
    String register(RegisterRequest registerRequest);
    String verifyPassword(VerifyRequest verifyRequest);
    String logout();
}
