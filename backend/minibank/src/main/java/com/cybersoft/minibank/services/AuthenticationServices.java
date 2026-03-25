package com.cybersoft.minibank.services;

import com.cybersoft.minibank.dto.LoginDto;

public interface AuthenticationServices {
    LoginDto  login(String email,String password);
}
