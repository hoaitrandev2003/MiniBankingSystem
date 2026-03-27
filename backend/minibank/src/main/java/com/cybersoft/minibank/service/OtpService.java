package com.cybersoft.minibank.service;

public interface OtpService {
    void saveOtp(String email, String otp);
    String getOtp(String email);
    void removeOtp(String email);
}
