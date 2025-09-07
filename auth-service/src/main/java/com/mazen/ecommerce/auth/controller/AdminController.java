package com.mazen.ecommerce.auth.controller;

import com.mazen.ecommerce.auth.dto.request.AdminRoleChangeRequest;
import com.mazen.ecommerce.auth.dto.response.ApiResponse;
import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.dto.response.UserStatsResponse;
import com.mazen.ecommerce.auth.model.enums.Role;
import com.mazen.ecommerce.auth.security.SecurityUtils;
import com.mazen.ecommerce.auth.service.AdminService;
import com.mazen.ecommerce.auth.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin Management", description = "Administrative user management endpoints")
public class AdminController {

    private final AdminService adminService;
    private final AuditService auditService;

    @Operation(summary = "Get all users", description = "Retrieve list of all users (Admin only)")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        try {
            List<UserResponse> users = adminService.getAllUsers();

            return ResponseEntity.ok(
                    ApiResponse.success("Users retrieved successfully", users));

        } catch (Exception e) {
            log.error("Failed to get all users", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get users: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get users by role", description = "Retrieve users filtered by role (Admin only)")
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable Role role) {

        try {
            List<UserResponse> users = adminService.getUsersByRole(role);

            return ResponseEntity.ok(
                    ApiResponse.success("Users retrieved successfully", users));

        } catch (Exception e) {
            log.error("Failed to get users by role: {}", role, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get users by role: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get user details", description = "Get detailed user information (Admin only)")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(@PathVariable Long userId) {

        try {
            UserResponse user = adminService.getUserDetails(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("User details retrieved successfully", user));

        } catch (Exception e) {
            log.error("Failed to get user details for ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user details: " + e.getMessage()));
        }
    }

    @Operation(summary = "Change user role", description = "Change a user's role (Admin only)")
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @PathVariable Long userId,
            @Valid @RequestBody AdminRoleChangeRequest request) {

        try {
            String adminEmail = SecurityUtils.getCurrentUserEmail();
            UserResponse oldUser = adminService.getUserDetails(userId);
            UserResponse updatedUser = adminService.changeUserRole(userId, request.getRole());

            auditService.logRoleChange(updatedUser.getEmail(), oldUser.getRole(),
                    updatedUser.getRole(), adminEmail);

            return ResponseEntity.ok(
                    ApiResponse.success("User role updated successfully", updatedUser));

        } catch (Exception e) {
            log.error("Failed to change user role for ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to change user role: " + e.getMessage()));
        }
    }

    @Operation(summary = "Toggle user status", description = "Enable/disable a user account (Admin only)")
    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<ApiResponse<Void>> toggleUserStatus(@PathVariable Long userId) {

        try {
            String adminEmail = SecurityUtils.getCurrentUserEmail();
            UserResponse user = adminService.getUserDetails(userId);

            adminService.toggleUserStatus(userId);

            auditService.logAccountStatusChange(user.getEmail(), !user.getIsEnabled(), adminEmail);

            return ResponseEntity.ok(
                    ApiResponse.success("User status toggled successfully", null));

        } catch (Exception e) {
            log.error("Failed to toggle user status for ID: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to toggle user status: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get dashboard statistics", description = "Get user statistics for admin dashboard")
    @GetMapping("/dashboard/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getDashboardStats() {

        try {
            UserStatsResponse stats = adminService.getDashboardStats();

            return ResponseEntity.ok(
                    ApiResponse.success("Dashboard statistics retrieved successfully", stats));

        } catch (Exception e) {
            log.error("Failed to get dashboard statistics", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get dashboard statistics: " + e.getMessage()));
        }
    }

    @Operation(summary = "Search users", description = "Search users by email or name (Admin only)")
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name) {

        try {
            // For now, return all users - can implement search logic later
            List<UserResponse> users = adminService.getAllUsers();

            // Simple filtering (can be enhanced with database queries)
            if (email != null && !email.trim().isEmpty()) {
                users = users.stream()
                        .filter(user -> user.getEmail().toLowerCase()
                                .contains(email.toLowerCase()))
                        .toList();
            }

            if (name != null && !name.trim().isEmpty()) {
                users = users.stream()
                        .filter(user -> user.getName().toLowerCase()
                                .contains(name.toLowerCase()))
                        .toList();
            }

            return ResponseEntity.ok(
                    ApiResponse.success("Search completed successfully", users));

        } catch (Exception e) {
            log.error("Failed to search users", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to search users: " + e.getMessage()));
        }
    }
}

