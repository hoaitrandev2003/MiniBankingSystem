package com.cybersoft.minibank.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String username;
    private String accessToken;
}
