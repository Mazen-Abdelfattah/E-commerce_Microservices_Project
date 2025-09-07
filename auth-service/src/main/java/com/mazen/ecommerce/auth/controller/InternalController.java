
// ================================
// INTERNAL CONTROLLER (FOR SERVICE-TO-SERVICE COMMUNICATION)
// ================================
package com.mazen.ecommerce.auth.controller;

import com.mazen.ecommerce.auth.dto.response.ApiResponse;
import com.mazen.ecommerce.auth.dto.response.TokenValidationResponse;
import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.service.AuthService;
import com.mazen.ecommerce.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
@Tag(name = "Internal API", description = "Internal endpoints for service-to-service communication")
public class InternalController {

    private final AuthService authService;
    private final UserService userService;

    @Operation(summary = "Validate token (Internal)", description = "Validate JWT token for other microservices")
    @PostMapping("/validate-token")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateTokenInternal(
            @RequestHeader("Authorization") String authHeader) {

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Invalid Authorization header format"));
            }

            String token = authHeader.substring(7);
            TokenValidationResponse response = authService.validateToken(token);

            return ResponseEntity.ok(
                    ApiResponse.success("Token validation completed", response));

        } catch (Exception e) {
            log.error("Internal token validation failed", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Token validation failed: " + e.getMessage()));
        }
    }

    @Operation(summary = "Get user by ID (Internal)", description = "Get user information by ID for other microservices")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByIdInternal(@PathVariable Long userId) {

        try {
            UserResponse user = userService.getUserById(userId);

            return ResponseEntity.ok(
                    ApiResponse.success("User retrieved successfully", user));

        } catch (Exception e) {
            log.error("Failed to get user by ID internally: {}", userId, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get user: " + e.getMessage()));
        }
    }

    @Operation(summary = "Check if user exists (Internal)", description = "Check if user exists by email for other microservices")
    @GetMapping("/users/exists")
    public ResponseEntity<ApiResponse<Boolean>> userExistsByEmail(@RequestParam String email) {

        try {
            boolean exists = authService.isUserEnabled(email);

            return ResponseEntity.ok(
                    ApiResponse.success("User existence check completed", exists));

        } catch (Exception e) {
            log.error("Failed to check user existence for email: {}", email, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to check user existence: " + e.getMessage()));
        }
    }
}


