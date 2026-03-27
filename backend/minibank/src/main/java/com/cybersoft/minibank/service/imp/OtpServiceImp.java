package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.service.OtpService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class OtpServiceImp implements OtpService {

    private Map<String,String> otpStorage= new HashMap<>();

    @Override
    public void saveOtp(String email, String otp) {
        otpStorage.put(email, otp);
    }

    @Override
    public String getOtp(String email) {
        return otpStorage.get(email);
    }

    @Override
    public void removeOtp(String email) {
        otpStorage.remove(email);
    }
}
