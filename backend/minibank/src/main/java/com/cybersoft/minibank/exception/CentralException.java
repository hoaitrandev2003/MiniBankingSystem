package com.cybersoft.minibank.exception;

import com.cybersoft.minibank.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class CentralException {

    @ExceptionHandler(exception = InvalidUserException.class)
    public ResponseEntity<?>  handleException(InvalidUserException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(exception.getMessage());
        baseResponse.setCode(401);
        return ResponseEntity.ok(baseResponse);
    }

    @ExceptionHandler(exception = InvalidNotValueUserException.class)
    public ResponseEntity<?>  handleException(InvalidNotValueUserException exception) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(exception.getMessage());
        baseResponse.setCode(500);
        return ResponseEntity.ok(baseResponse);
    }

    // Ném lỗi cho User Register
    @ExceptionHandler(exception = InvalidUserRegisterException.class)
    public ResponseEntity<?>  handleException(InvalidUserRegisterException invalidUserRegisterException) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(invalidUserRegisterException.getMessage());
        baseResponse.setCode(401);
        return ResponseEntity.ok(baseResponse);
    }

    @ExceptionHandler(exception = InvalidRefreshTokenException.class)
    public ResponseEntity<?>  handleException(InvalidRefreshTokenException invalidRefreshTokenException) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(invalidRefreshTokenException.getMessage());
        baseResponse.setCode(401);
        return ResponseEntity.ok(baseResponse);
    }

}
