package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepostitory extends JpaRepository<RoleEntity,Integer> {
}
