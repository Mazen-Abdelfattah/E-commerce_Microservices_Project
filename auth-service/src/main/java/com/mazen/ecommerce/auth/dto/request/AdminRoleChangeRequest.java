package com.mazen.ecommerce.auth.dto.request;

import com.mazen.ecommerce.auth.model.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminRoleChangeRequest {

    @NotNull(message = "Role is required")
    private Role role;
}