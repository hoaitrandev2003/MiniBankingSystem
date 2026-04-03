package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.dto.RegisterDTO;
import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.dto.VerifyDTO;
import com.cybersoft.minibank.payload.request.LoginRequest;
import com.cybersoft.minibank.payload.response.BaseResponse;
import com.cybersoft.minibank.service.AuthenticationServices;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Autowired
    private AuthenticationServices authenticationServices;

    private ObjectMapper objectMapper = new ObjectMapper();
    //Đăng nhập
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {

        UserDTO user = authenticationServices.login(loginRequest);

        // Chuyển từ Object sang String
        String jsonUser = objectMapper.writeValueAsString(user);

        // Add các thông tin vào token
        String token = jwtHelper.generateToken(jsonUser);

        // trả về
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(token);

        return ResponseEntity.ok(baseResponse);
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
