package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.dto.LoginDto;
import com.cybersoft.minibank.services.AuthenticationServices;
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

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestParam String email, @RequestParam String password){
        LoginDto result = authenticationServices.login(email, password);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(){
        return ResponseEntity.ok("/sign-up");
    }
}
