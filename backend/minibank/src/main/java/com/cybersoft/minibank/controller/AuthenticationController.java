package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.dto.LogInDTO;
import com.cybersoft.minibank.payload.request.*;
import com.cybersoft.minibank.payload.response.BaseResponse;
import com.cybersoft.minibank.service.AuthenticationServices;
import com.cybersoft.minibank.service.EmailService;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Autowired
    private AuthenticationServices authenticationServices;

    @Autowired
    private EmailService emailService;

    private ObjectMapper objectMapper = new ObjectMapper();

    //Đăng nhập
    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
        LogInDTO user = authenticationServices.login(loginRequest);

        // trả về
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(user);

        return ResponseEntity.ok(baseResponse);
    }

    // Đăng kí
    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody RegisterRequest registerRequest) {
        String result = authenticationServices.register(registerRequest);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(result);

        return ResponseEntity.ok(baseResponse);
    }

    // Xác thực
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPassword(@RequestBody VerifyRequest verifyRequest) {
        // Gọi hàm verify trong ServiceImp mà bạn vừa viết
        String result = authenticationServices.verifyPassword(verifyRequest);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(result);

        return ResponseEntity.ok(baseResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        String result = authenticationServices.logout();
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(result);
        return  ResponseEntity.ok(baseResponse);
    }
}
