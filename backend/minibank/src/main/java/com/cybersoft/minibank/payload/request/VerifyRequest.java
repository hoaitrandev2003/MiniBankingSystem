package com.cybersoft.minibank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequest {
    private String password;
    private String email;
}
