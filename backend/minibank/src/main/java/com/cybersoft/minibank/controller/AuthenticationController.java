package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.dto.LoginDTO;
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
    public ResponseEntity<?> signIn(@RequestParam String username, @RequestParam String password){
        LoginDTO result = authenticationServices.login(username, password);
        return ResponseEntity.ok(result);
    }

    //Đăng kí
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestParam String email,@RequestParam String username, @RequestParam String password){
        String result = authenticationServices.register(email,username,password);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-up/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp){
        String verifyOtp = authenticationServices.verifyOtp(email,otp);
        return ResponseEntity.ok(verifyOtp);
    }
}
