package com.mazen.ecommerce.auth.security;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordConfig {

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 100;

    // Password strength requirements (can be enhanced later)
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH ||
                password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }

        // Basic requirements - can be enhanced
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasLetter && hasDigit;
    }

    public static String getPasswordRequirements() {
        return "Password must be between " + MIN_PASSWORD_LENGTH + " and " +
                MAX_PASSWORD_LENGTH + " characters and contain at least one letter and one digit";
    }
}

