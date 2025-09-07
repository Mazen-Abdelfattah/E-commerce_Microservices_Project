
/** It might be like the UserService but the difference that
 *  it delegates to UserService but adds admin-specific logging

 * Dashboard statistics for admin interface

 * User management operations with proper audit trails
*/
package com.mazen.ecommerce.auth.service;

import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.dto.response.UserStatsResponse;
import com.mazen.ecommerce.auth.model.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserService userService;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Admin requesting all users list");
        return userService.getAllUsers();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        log.info("Admin requesting users by role: {}", role);
        return userService.getUsersByRole(role);
    }

    public UserResponse changeUserRole(Long userId, Role newRole) {
        log.info("Admin changing user role for userId: {} to role: {}", userId, newRole);
        return userService.changeUserRole(userId, newRole);
    }

    public void toggleUserStatus(Long userId) {
        log.info("Admin toggling user status for userId: {}", userId);
        userService.toggleUserStatus(userId);
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getDashboardStats() {
        log.info("Admin requesting dashboard statistics");
        return userService.getUserStats();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserDetails(Long userId) {
        log.info("Admin requesting user details for userId: {}", userId);
        return userService.getUserById(userId);
    }
}
