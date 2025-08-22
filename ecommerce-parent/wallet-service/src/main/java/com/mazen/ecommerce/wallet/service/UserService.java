package com.mazen.ecommerce.wallet.service;

import com.mazen.ecommerce.wallet.dto.user.AuthResponse;
import com.mazen.ecommerce.wallet.dto.user.LoginRequest;
import com.mazen.ecommerce.wallet.dto.user.RegisterRequest;
import com.mazen.ecommerce.wallet.dto.user.UserResponse;
import com.mazen.ecommerce.wallet.model.User;
import com.mazen.ecommerce.wallet.model.Wallet;
import com.mazen.ecommerce.wallet.model.enums.Role;
import com.mazen.ecommerce.wallet.repository.UserRepository;
import com.mazen.ecommerce.wallet.repository.WalletRepository;
import com.mazen.ecommerce.wallet.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtService;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role role = Role.USER; // default
        if (request.getRole() != null && request.getRole().equals("SELLER")) {
            role = Role.SELLER;
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        // Optional: create a default wallet
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .build();

        if(walletRepository.existsById(wallet.getId()))user.getWallets().add(wallet);

        User saved = userRepository.save(user);

        return UserResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .role(saved.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
