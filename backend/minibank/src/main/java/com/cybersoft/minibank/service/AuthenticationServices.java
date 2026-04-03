package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.dto.VerifyDTO;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.response.BaseResponse;

public interface AuthenticationServices {
    UserDTO login(LoginRequest loginRequest);
    String register(RegisterDTO registerDTO);
    String verifyOtp(VerifyDTO verifyDTO);
}
