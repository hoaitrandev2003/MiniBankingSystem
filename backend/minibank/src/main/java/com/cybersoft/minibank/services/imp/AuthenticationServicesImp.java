package com.cybersoft.minibank.services.imp;

import com.cybersoft.minibank.dto.LoginDto;
import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.services.AuthenticationServices;
import com.cybersoft.minibank.utils.JwtUtilHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServicesImp implements AuthenticationServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtilHelper jwtHelper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginDto login(String email, String password) {
        //Kiểm tra xem có trong database hay không
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserEntity user = userRepository.findByEmail(email);

        String jwt = jwtHelper.generateToken(email);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(user.getEmail());
        loginDto.setToken(jwt);

        return loginDto;
    }

}
