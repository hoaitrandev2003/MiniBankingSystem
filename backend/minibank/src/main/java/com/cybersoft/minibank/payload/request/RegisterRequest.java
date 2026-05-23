package com.cybersoft.minibank.payload.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String userName;
    private String fullName;
    private String phone;
    private String gender;
    private LocalDate dateOfBirth;
    private String identityNumber;
    private String address;
}
