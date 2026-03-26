package com.cybersoft.minibank.dto;

import com.cybersoft.minibank.entity.RoleEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterDTO {
    private String username;
    private String email;
    private String password;
}
