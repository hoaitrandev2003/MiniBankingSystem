package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.entity.Role;
import com.cybersoft.minibank.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRole(){
        List<Role> list = roleService.getAllRole();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/roles")
    public ResponseEntity<?> createRole(@RequestBody Role role){
        Role created = roleService.createRole(role);
        return ResponseEntity.ok(created);
    }
    @DeleteMapping("/roles")
    public ResponseEntity<?> deleteRole(@PathVariable int id){
        roleService.deleteRole(id);
        return ResponseEntity.ok("Xóa Role Id: " + id + "thành công");
    }
}
