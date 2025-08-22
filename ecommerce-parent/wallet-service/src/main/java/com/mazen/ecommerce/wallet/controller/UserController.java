package com.mazen.ecommerce.wallet.controller;

import com.mazen.ecommerce.wallet.dto.user.AuthResponse;
import com.mazen.ecommerce.wallet.dto.user.LoginRequest;
import com.mazen.ecommerce.wallet.dto.user.RegisterRequest;
import com.mazen.ecommerce.wallet.dto.user.UserResponse;
import com.mazen.ecommerce.wallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
