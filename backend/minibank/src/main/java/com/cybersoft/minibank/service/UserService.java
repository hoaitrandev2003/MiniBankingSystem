package com.cybersoft.minibank.service;

import com.cybersoft.minibank.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserEntity createUser(UserEntity userEntity);
    List<UserEntity> getAllUser();
    Optional<UserEntity> getUserById(int id);
    UserEntity updateUser(int id, UserEntity userEntity);
    void deleteUser(int id);
}
