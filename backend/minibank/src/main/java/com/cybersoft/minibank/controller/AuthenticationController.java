package com.cybersoft.minibank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(){
        return ResponseEntity.ok("/sign-in");
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(){
        return ResponseEntity.ok("/sign-up");
    }
}
