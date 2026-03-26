package com.cybersoft.minibank.service.imp;

import com.cybersoft.minibank.entity.RoleEntity;
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
    public List<RoleEntity> getAllRole() {
        return roleRepostitory.findAll();
    }

    @Override
    public RoleEntity createRole(RoleEntity roleEntity) {
        return roleRepostitory.save(roleEntity);
    }

    @Override
    public void deleteRole(int id) {
        RoleEntity roleEntity = roleRepostitory.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id: " + id));
        roleRepostitory.delete(roleEntity);
    }
}
