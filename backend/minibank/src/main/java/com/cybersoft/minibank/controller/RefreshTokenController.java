package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.payload.request.RefreshRequest;
import com.cybersoft.minibank.payload.response.BaseResponse;
import com.cybersoft.minibank.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/refresh")
public class RefreshTokenController {
    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {

        String newAccessToken = refreshTokenService.refreshToken(request);

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage("success");
        baseResponse.setData(newAccessToken);

        return ResponseEntity.ok(baseResponse);
    }
}
