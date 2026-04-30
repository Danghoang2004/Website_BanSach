package com.example.web_bansach.module.user.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.auth.dto.request.LoginRequest;
import com.example.web_bansach.module.auth.dto.request.UserRequest;
import com.example.web_bansach.module.auth.dto.response.LoginResponse;
import com.example.web_bansach.module.user.dto.request.AdminUpdateUserRequest;
import com.example.web_bansach.module.user.dto.request.UpdateUserRequest;
import com.example.web_bansach.module.user.dto.response.UserResponse;
import com.example.web_bansach.module.user.entity.Users;
import com.example.web_bansach.module.user.repository.UserRepository;
import com.example.web_bansach.security.jwt.JwtService;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Users layNguoiDungTheoId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID người dùng không hợp lệ");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    @Transactional
    public Users taoTaiKhoanMoi(UserRequest request) {
        Users existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            throw new BusinessException("Tên tài khoản đã được sử dụng");
        }

        Users existingEmail = userRepository.findByEmail(request.getEmail());
        if (existingEmail != null) {
            throw new BusinessException("Email đã được sử dụng");
        }

        Users newUser = new Users();
        newUser.setUsername(request.getUsername().trim());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setEmail(request.getEmail().trim());
        newUser.setPhone(request.getPhone());
        newUser.setFullName(request.getFullName());
        newUser.setAddress(request.getAddress());
        newUser.setIsActive(true);

        return userRepository.save(newUser);
    }

    @Transactional
    public LoginResponse dangNhap(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));
        } catch (Exception e) {
            throw new BusinessException("Sai tên đăng nhập hoặc mật khẩu");
        }

        Users user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng");
        }

        if (!user.getIsActive()) {
            throw new BusinessException("Tài khoản này đã bị khóa");
        }

        String token = jwtService.generateToken(request.getUsername());
        Set<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet());

        return new LoginResponse(token, user.getId(), user.getUsername(), roles);
    }

    @Transactional
    public Page<UserResponse> getAllUsersPagination(Integer pageNumber, Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = 0;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }

        PageRequest pageable = PageRequest.of(pageNumber, pageSize);
        Page<Users> usersPage = userRepository.findAll(pageable);

        return usersPage.map(this::convertToUserResponse);
    }

    private UserResponse convertToUserResponse(Users users) {
        UserResponse response = new UserResponse();
        response.setUserId(users.getId());
        response.setUsername(users.getUsername());
        response.setEmail(users.getEmail());
        response.setFullName(users.getFullName());
        response.setPhone(users.getPhone());
        response.setAddress(users.getAddress());
        response.setIsActive(users.getIsActive());
        response.setCreatedAt(users.getCreatedAt());
        response.setRoles(users.getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()));
        return response;
    }

    @Transactional
    public void updateCurrentUserProfile(String username, UpdateUserRequest update) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Người dùng không tồn tại");
        }
        updateUserProfileById(user.getId(), update);
    }

    @Transactional
    public void updateUserProfileById(Long userId, UpdateUserRequest update) {
        Users userData = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        userData.setFullName(update.getFullName());

        if (update.getEmail() != null && !update.getEmail().equals(userData.getEmail())) {
            Users usersEmail = userRepository.findByEmail(update.getEmail());
            if (usersEmail != null) {
                throw new BusinessException("Email đã được dùng, vui lòng chọn email khác");
            }
            userData.setEmail(update.getEmail());
        }

        if (update.getPhone() != null && !update.getPhone().equals(userData.getPhone())) {
            userData.setPhone(update.getPhone());
        }

        if (update.getAddress() != null && !update.getAddress().equals(userData.getAddress())) {
            userData.setAddress(update.getAddress());
        }

        userRepository.save(userData);
    }

    public void xoaNguoiDungTheoId(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException("ID người dùng không hợp lệ");
        }

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        userRepository.deleteById(user.getId());
    }

    @Transactional
    public void capNhatNguoiDungAdmin(Long userId, AdminUpdateUserRequest request) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("ID người dùng không hợp lệ");
        }

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (request.getFullName() != null && !request.getFullName().isEmpty()) {
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user.setPhone(request.getPhone());
        }

        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            user.setAddress(request.getAddress());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            Users emailExists = userRepository.findByEmail(request.getEmail());
            if (emailExists != null) {
                throw new BusinessException("Email đã được sử dụng");
            }
            user.setEmail(request.getEmail());
        }

        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }
        userRepository.save(user);
    }
}
