package com.cybersoft.minibank.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegisterDTO {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String identityNumber;
    private String address;
}
