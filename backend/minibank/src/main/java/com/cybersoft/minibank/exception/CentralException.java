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
}
