package com.cybersoft.minibank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity(name = "roles")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "roleEntity")
    private Set<UserEntity> userEntities;
}