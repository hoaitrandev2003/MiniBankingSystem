package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.Role;
import com.cybersoft.minibank.repository.RoleRepostitory;
import com.cybersoft.minibank.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImp implements RoleService {
    @Autowired
    private RoleRepostitory roleRepostitory;

    @Override
    public List<Role> getAllRole() {
        return roleRepostitory.findAll();
    }

    @Override
    public Role createRole(Role role) {
        return roleRepostitory.save(role);
    }

    @Override
    public void deleteRole(int id) {
        Role role = roleRepostitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id: " + id));
        roleRepostitory.delete(role);
    }
}
