package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> {
    UserEntity findByEmail(String email);
    Optional<UserEntity> findByUserName(String userName);
}
