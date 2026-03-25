package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    UserEntity findByEmail(String email);
}
