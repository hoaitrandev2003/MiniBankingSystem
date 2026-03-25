package com.cybersoft.minibank.controller;

import com.cybersoft.minibank.entity.User;
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
        List<User> list = userService.getAllUser();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user){
        User user1 = userService.createUser(user);
        return ResponseEntity.ok(user1);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable int id,@RequestBody User user){
        return ResponseEntity.ok(userService.updateUser(id,user));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(" Xóa user "+ id + " thành công! ");
    }





}
