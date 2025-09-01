package com.mazen.ecommerce.auth.dto.response;

import com.mazen.ecommerce.auth.model.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Boolean isEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
