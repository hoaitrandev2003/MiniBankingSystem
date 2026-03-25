package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.dto.LoginDto;
import com.cybersoft.minibank.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    UserEntity findByEmail(String email);
}