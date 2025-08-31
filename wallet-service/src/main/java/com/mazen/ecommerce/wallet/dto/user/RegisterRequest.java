package com.mazen.ecommerce.wallet.dto.user;

import com.mazen.ecommerce.wallet.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank private String email;

    @NotBlank private String password;

    private Role role;
}
