package com.cybersoft.minibank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyRequest {
    private String userName;
    private String email;
    private String OldPassword;
    private String NewPassword;
}
