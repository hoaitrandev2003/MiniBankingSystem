package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.entity.User;
import com.cybersoft.minibank.repository.UserRepository;
import com.cybersoft.minibank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(int id, User user) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id: " + id));

            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setFullName(user.getFullName());
            updatedUser.setPhone(user.getPhone());
            updatedUser.setGender(user.getGender());
            updatedUser.setDateOfBirth(user.getDateOfBirth());
            updatedUser.setAddress(user.getAddress());
            updatedUser.setIdentityNumber(user.getIdentityNumber());

        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id: " + id));
            userRepository.delete(user);
    }
}
