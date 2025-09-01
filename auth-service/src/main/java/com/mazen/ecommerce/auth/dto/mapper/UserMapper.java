package com.mazen.ecommerce.auth.dto.mapper;

import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isEnabled(user.getIsEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public User toUser(com.mazen.ecommerce.auth.dto.request.RegisterRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole() != null ? request.getRole() :
                        com.mazen.ecommerce.auth.model.enums.Role.USER)
                .build();
    }
}
