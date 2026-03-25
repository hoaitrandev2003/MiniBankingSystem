package com.cybersoft.minibank.service;

import com.cybersoft.minibank.entity.RoleEntity;

import java.util.List;

public interface RoleService {
    List<RoleEntity> getAllRole();
    RoleEntity createRole(RoleEntity roleEntity);
    void deleteRole(int id);
}
