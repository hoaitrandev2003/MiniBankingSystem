package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepostitory extends JpaRepository<Role,Integer> {
}
