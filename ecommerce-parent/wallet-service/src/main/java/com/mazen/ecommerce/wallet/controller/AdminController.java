package com.mazen.ecommerce.wallet.controller;

import com.mazen.ecommerce.wallet.dto.user.UserResponse;
import com.mazen.ecommerce.wallet.model.enums.Role;
import com.mazen.ecommerce.wallet.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> listUsers() {
        return ResponseEntity.ok(adminService.listAllUsers());
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> changeRole(
            @PathVariable Long id,
            @RequestParam Role role
    ) {
        return ResponseEntity.ok(adminService.changeRole(id, role));
    }
}
