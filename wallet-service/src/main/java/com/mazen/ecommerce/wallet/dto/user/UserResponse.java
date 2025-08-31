package com.mazen.ecommerce.wallet.dto.user;

import com.mazen.ecommerce.wallet.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}