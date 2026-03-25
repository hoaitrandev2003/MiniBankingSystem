package com.cybersoft.minibank.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String password;
    private String full_name;
    private String phone;
    private String gender;
    private Date date_of_birth;
    private String identity_number;
    private String address;
    private String failed_login_attempt;
    private String status;
//    private int role_id;
    private String created_at;
    private String updated_at;
}
