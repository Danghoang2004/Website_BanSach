package com.example.web_bansach.module.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;
import com.example.web_bansach.module.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tai-khoan")
public class AuthController {

    @Autowired
    private UserService service;

    @PostMapping("/dang-ky")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRequest request) {
        service.taoTaiKhoanMoi(request);
        return ResponseEntity.ok("Tạo tài khoản thành công");
    }

    @PostMapping("/dang-nhap")
    public ResponseEntity<LoginResponse> dangNhap(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = service.dangNhap(request);
        return ResponseEntity.ok(response);
    }
}
