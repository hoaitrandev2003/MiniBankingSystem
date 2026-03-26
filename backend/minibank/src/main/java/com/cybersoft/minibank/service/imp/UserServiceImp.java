package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.UserEntity;
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
    public UserEntity createUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public List<UserEntity> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserEntity> getUserById(int id) {
        return userRepository.findById(id);
    }

    @Override
    public UserEntity updateUser(int id, UserEntity userEntity) {
        UserEntity updatedUserEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id: " + id));

            updatedUserEntity.setEmail(userEntity.getEmail());
            updatedUserEntity.setPassword(userEntity.getPassword());
            updatedUserEntity.setFullName(userEntity.getFullName());
            updatedUserEntity.setPhone(userEntity.getPhone());
            updatedUserEntity.setGender(userEntity.getGender());
            updatedUserEntity.setDateOfBirth(userEntity.getDateOfBirth());
            updatedUserEntity.setAddress(userEntity.getAddress());
            updatedUserEntity.setIdentityNumber(userEntity.getIdentityNumber());

        return userRepository.save(updatedUserEntity);
    }

    @Override
    public void deleteUser(int id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user id: " + id));
            userRepository.delete(userEntity);
    }
}
