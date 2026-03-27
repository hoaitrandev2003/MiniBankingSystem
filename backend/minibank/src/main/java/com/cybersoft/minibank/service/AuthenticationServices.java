package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.LoginDTO;

public interface AuthenticationServices {
    LoginDTO login(String email, String password);
    String register(String username,String email,String password);
    String verifyOtp(String email, String otp);
}
