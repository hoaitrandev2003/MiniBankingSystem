package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.entity.UserEntity;
import com.cybersoft.minibank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUser(){
        List<UserEntity> list = userService.getAllUser();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity){
        UserEntity userEntity1 = userService.createUser(userEntity);
        return ResponseEntity.ok(userEntity1);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,@RequestBody UserEntity userEntity){
        return ResponseEntity.ok(userService.updateUser(id, userEntity));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(" Xóa user "+ id + " thành công! ");
    }





}
