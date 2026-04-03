package com.cybersoft.minibank.mapper;

import com.cybersoft.minibank.dto.UserDTO;
import com.cybersoft.minibank.entity.UserEntity;

public class UserMapper {
    public static UserDTO mapDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setPassword(userEntity.getPassword());
        userDTO.setRole(userEntity.getRoleEntity().getName());

        return userDTO;
    }
}
