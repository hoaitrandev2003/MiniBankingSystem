package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.VerifyDTO;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.response.LoginRespone;
import com.cybersoft.minibank.service.AuthenticationServices;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Autowired
    private AuthenticationServices authenticationServices;

    //Đăng nhập
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        LoginRespone result = authenticationServices.login(loginRequest);
        return ResponseEntity.ok(result);
    }

    //Đăng kí
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody RegisterDTO registerDTO) {
        String result = authenticationServices.register(registerDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-up/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyDTO verifyDTO) {
        String verifyOtp = authenticationServices.verifyOtp(verifyDTO);
        return ResponseEntity.ok(verifyOtp);
    }
}
