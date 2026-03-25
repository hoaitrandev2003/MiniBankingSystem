package com.cybersoft.minibank.service;

import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    List<User> getAllUser();
    Optional<User> getUserById(int id);
    User updateUser(int id, User user);
    void deleteUser(int id);
}
