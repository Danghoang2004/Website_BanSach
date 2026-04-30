package com.example.web_bansach.module.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.web_bansach.module.user.dto.request.UpdateUserRequest;
import com.example.web_bansach.module.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserProfileController {

    @Autowired
    private UserService service;

    // Cập nhật thông tin tài khoản của người dùng hiện tại
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserRequest UpdateUserRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        service.updateCurrentUserProfile(username, UpdateUserRequest);
        return ResponseEntity.ok("Cập nhật thông tin thành công");
    }
}
