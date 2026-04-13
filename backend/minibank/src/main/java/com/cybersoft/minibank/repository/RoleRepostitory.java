package com.cybersoft.minibank.repository;

import com.cybersoft.minibank.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepostitory extends JpaRepository<RoleEntity,Integer> {
    Optional<RoleEntity> findByName(String name);
}
