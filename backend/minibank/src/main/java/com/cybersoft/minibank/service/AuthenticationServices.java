package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.LogInDTO;
import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.request.RegisterRequest;

public interface AuthenticationServices {
    LogInDTO login(LoginRequest loginRequest);
    String register(RegisterRequest registerRequest);
    String verifyPassword(String email, String userInputOtp);
}
