package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.VerifyDTO;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.response.LoginRespone;

public interface AuthenticationServices {
    LoginRespone login(LoginRequest loginRequest);
    String register(RegisterDTO registerDTO);
    String verifyOtp(VerifyDTO verifyDTO);
}
