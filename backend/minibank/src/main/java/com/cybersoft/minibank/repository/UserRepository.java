package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    UserEntity findByEmail(String email);
    UserEntity findByUserName(String userName);
}
