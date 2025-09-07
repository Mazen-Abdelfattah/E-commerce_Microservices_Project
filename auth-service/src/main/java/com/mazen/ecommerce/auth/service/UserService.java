package com.mazen.ecommerce.auth.service;

import com.mazen.ecommerce.auth.dto.mapper.UserMapper;
import com.mazen.ecommerce.auth.dto.request.UpdateUserRequest;
import com.mazen.ecommerce.auth.dto.request.ChangePasswordRequest;
import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.dto.response.UserStatsResponse;
import com.mazen.ecommerce.auth.model.User;
import com.mazen.ecommerce.auth.model.enums.Role;
import com.mazen.ecommerce.auth.repository.UserRepository;
import com.mazen.ecommerce.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmailAndIsEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateCurrentUserProfile(UpdateUserRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmailAndIsEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if new email is already in use
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already in use");
            }
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        log.info("User profile updated successfully for user: {}", savedUser.getEmail());

        return userMapper.toUserResponse(savedUser);
    }

    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmailAndIsEnabledTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Encode and set new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role oldRole = user.getRole();
        user.setRole(newRole);
        User savedUser = userRepository.save(user);

        log.info("User role changed from {} to {} for user: {}",
                oldRole, newRole, savedUser.getEmail());

        return userMapper.toUserResponse(savedUser);
    }

    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsEnabled(!user.getIsEnabled());
        User savedUser = userRepository.save(user);

        log.info("User status toggled to {} for user: {}",
                savedUser.getIsEnabled() ? "enabled" : "disabled", savedUser.getEmail());
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);

        return UserStatsResponse.builder()
                .totalUsers((long) userRepository.findAll().size())
                .activeUsers((long) userRepository.findByIsEnabledTrue().size())
                .adminUsers(userRepository.countByRole(Role.ADMIN))
                .sellerUsers(userRepository.countByRole(Role.SELLER))
                .regularUsers(userRepository.countByRole(Role.USER))
                .newUsersThisWeek((long) userRepository.findUsersCreatedSince(oneWeekAgo).size())
                .newUsersThisMonth((long) userRepository.findUsersCreatedSince(oneMonthAgo).size())
                .build();
    }
}

