
// ================================
// AUTH SERVICE (MAIN SERVICE)
// ================================

package com.mazen.ecommerce.auth.service;

import com.mazen.ecommerce.auth.dto.mapper.UserMapper;
import com.mazen.ecommerce.auth.dto.request.LoginRequest;
import com.mazen.ecommerce.auth.dto.request.RegisterRequest;
import com.mazen.ecommerce.auth.dto.response.AuthResponse;
import com.mazen.ecommerce.auth.dto.response.TokenValidationResponse;
import com.mazen.ecommerce.auth.dto.response.UserResponse;
import com.mazen.ecommerce.auth.model.RefreshToken;
import com.mazen.ecommerce.auth.model.User;
import com.mazen.ecommerce.auth.model.enums.Role;
import com.mazen.ecommerce.auth.repository.UserRepository;
import com.mazen.ecommerce.auth.security.JwtService;
import com.mazen.ecommerce.auth.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserMapper userMapper;

    public UserResponse register(RegisterRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Create new user
        User user = User.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .isEnabled(true)
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        return userMapper.toUserResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Get user details
            User user = userRepository.findByEmailAndIsEnabledTrue(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found or disabled"));

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            log.info("User logged in successfully: {}", user.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                    .user(userMapper.toUserResponse(user))
                    .build();

        } catch (Exception e) {
            log.error("Login failed for user: {}", request.getEmail(), e);
            throw new RuntimeException("Invalid email or password");
        }
    }

    public AuthResponse refreshToken(String refreshTokenStr) {
        log.info("Attempting to refresh token");

        return refreshTokenService.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateAccessToken(user);

                    log.info("Token refreshed successfully for user: {}", user.getEmail());

                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(refreshTokenStr) // Keep same refresh token
                            .tokenType("Bearer")
                            .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                            .user(userMapper.toUserResponse(user))
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database"));
    }

    public void logout() {
        String email = SecurityUtils.getCurrentUserEmail();
        if (email != null) {
            User user = userRepository.findByEmailAndIsEnabledTrue(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            refreshTokenService.deleteByUser(user);
            log.info("User logged out successfully: {}", email);
        }
    }

    @Transactional(readOnly = true)
    public TokenValidationResponse validateToken(String token) {
        try {
            if (jwtService.validateToken(token)) {
                String email = jwtService.getEmailFromToken(token);
                Long userId = jwtService.getUserIdFromToken(token);
                String roleStr = jwtService.getRoleFromToken(token);
                Role role = Role.valueOf(roleStr);

                return TokenValidationResponse.builder()
                        .valid(true)
                        .userId(userId)
                        .email(email)
                        .role(role)
                        .build();
            }
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
        }

        return TokenValidationResponse.builder()
                .valid(false)
                .message("Invalid or expired token")
                .build();
    }

    @Transactional(readOnly = true)
    public boolean isUserEnabled(String email) {
        return userRepository.findByEmail(email)
                .map(User::getIsEnabled)
                .orElse(false);
    }
}
