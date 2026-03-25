package com.cybersoft.minibank.service;

import com.cybersoft.minibank.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAllRole();
    Role createRole(Role role);
    void deleteRole(int id);
}
