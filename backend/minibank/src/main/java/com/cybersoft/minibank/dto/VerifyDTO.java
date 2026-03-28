package com.cybersoft.minibank.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyDTO {
    private String otp;
    private String email;
}
