package com.mazen.ecommerce.wallet.client.dto;

import com.mazen.ecommerce.wallet.client.enums.Role;
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

