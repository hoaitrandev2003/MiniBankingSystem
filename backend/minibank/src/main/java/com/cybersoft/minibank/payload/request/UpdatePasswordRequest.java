package com.cybersoft.minibank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String username;
    private String oldPassword;
    private String newPassword;
}
