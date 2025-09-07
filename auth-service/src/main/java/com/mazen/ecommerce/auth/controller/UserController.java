package com.mazen.ecommerce.auth.controller;

import com.mazen.ecommerce.auth.dto.request.ChangePasswordRequest;
import com.mazen.ecommerce.auth.dto.request.UpdateUserRequest;
import com.mazen.ecommerce.auth.dto.response.ApiResponse;
import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.service.AuditService;
import com.mazen.ecommerce.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;
    private final AuditService auditService;

    @Operation(summary = "Get current user profile", description = "Get authenticated user's profile information")
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUserProfile() {

        try {
            UserResponse userResponse = userService.getCurrentUserProfile();

            return ResponseEntity.ok(
                    ApiResponse.success("Profile retrieved successfully", userResponse));

        } catch (Exception e) {
            log.error("Failed to get current user profile", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get profile: " + e.getMessage()));
        }
    }

    @Operation(summary = "Update current user profile", description = "Update authenticated user's profile information")
    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUserProfile(
            @Valid @RequestBody UpdateUserRequest request) {

        try {
            UserResponse userResponse = userService.updateCurrentUserProfile(request);

            return ResponseEntity.ok(
                    ApiResponse.success("Profile updated successfully", userResponse));

        } catch (Exception e) {
            log.error("Failed to update user profile", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to update profile: " + e.getMessage()));
        }
    }

    @Operation(summary = "Change password", description = "Change authenticated user's password")
    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        try {
            String userEmail = com.mazen.ecommerce.auth.security.SecurityUtils.getCurrentUserEmail();
            userService.changePassword(request);
            auditService.logPasswordChange(userEmail);

            return ResponseEntity.ok(
                    ApiResponse.success("Password changed successfully", null));

        } catch (Exception e) {
            log.error("Failed to change password", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to change password: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get user by ID", description = "Get user information by user ID (Admin or self only)")
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {

        try {
            UserResponse userResponse = userService.getUserById(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("User retrieved successfully", userResponse));

        } catch (Exception e) {
            log.error("Failed to get user by ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }
}

