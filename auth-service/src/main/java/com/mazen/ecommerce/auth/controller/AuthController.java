package com.mazen.ecommerce.auth.controller;

import com.mazen.ecommerce.auth.dto.request.LoginRequest;
import com.mazen.ecommerce.auth.dto.request.RefreshTokenRequest;
import com.mazen.ecommerce.auth.dto.request.RegisterRequest;
import com.mazen.ecommerce.auth.dto.response.ApiResponse;
import com.mazen.ecommerce.auth.dto.response.AuthResponse;
import com.mazen.ecommerce.auth.dto.response.TokenValidationResponse;
import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.service.AuthService;
import com.mazen.ecommerce.auth.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;
    private final AuditService auditService;

    @Operation(summary = "Register a new user", description = "Create a new user account")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        try {
            log.info("Registration attempt for email: {}", request.getEmail());

            UserResponse userResponse = authService.register(request);
            auditService.logUserRegistration(userResponse.getEmail(), userResponse.getRole());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully", userResponse));

        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Registration failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "User login", description = "Authenticate user and return access/refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        try {
            log.info("Login attempt for email: {}", request.getEmail());

            AuthResponse authResponse = authService.login(request);
            auditService.logUserLogin(request.getEmail(), getClientIpAddress(httpRequest));

            return ResponseEntity.ok(
                    ApiResponse.success("Login successful", authResponse));

        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            auditService.logFailedLogin(request.getEmail(), e.getMessage(),
                    getClientIpAddress(httpRequest));

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Refresh access token", description = "Generate new access token using refresh token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {

        try {
            log.info("Token refresh attempt");

            AuthResponse authResponse = authService.refreshToken(request.getRefreshToken());

            return ResponseEntity.ok(
                    ApiResponse.success("Token refreshed successfully", authResponse));

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Token refresh failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Validate token", description = "Validate JWT token (used by other services)")
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @RequestParam String token) {

        try {
            TokenValidationResponse response = authService.validateToken(token);
            auditService.logTokenValidation(response.isValid(), response.getEmail());

            return ResponseEntity.ok(
                    ApiResponse.success("Token validation completed", response));

        } catch (Exception e) {
            log.error("Token validation error", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token validation failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "User logout", description = "Logout current user and invalidate refresh token")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {

        try {
            String userEmail = com.mazen.ecommerce.auth.security.SecurityUtils.getCurrentUserEmail();
            authService.logout();
            auditService.logUserLogout(userEmail);

            return ResponseEntity.ok(
                    ApiResponse.success("Logout successful", null));

        } catch (Exception e) {
            log.error("Logout failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Logout failed: " + e.getMessage()));
        }
    }

    // Health check endpoint
    @Operation(summary = "Health check", description = "Check if auth service is running")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(
                ApiResponse.success("Auth service is running", "OK"));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor == null || xForwardedFor.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xForwardedFor.split(",")[0].trim();
    }
}

