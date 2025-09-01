package com.mazen.ecommerce.auth.dto.response;

import com.mazen.ecommerce.auth.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {

    private boolean valid;
    private Long userId;
    private String email;
    private Role role;
    private String message; // For invalid tokens
}

