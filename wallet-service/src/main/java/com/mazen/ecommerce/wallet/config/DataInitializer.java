package com.mazen.ecommerce.wallet.config;

import com.mazen.ecommerce.wallet.model.User;
import com.mazen.ecommerce.wallet.model.enums.Role;
import com.mazen.ecommerce.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedAdmin() {
        return args -> {
            if (userRepository.findByEmail("admin@ecom.com").isEmpty()) {
                User admin = User.builder()
                        .name("Super Admin")
                        .email("admin@ecom.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Default admin created: admin@ecom.com / admin123");
            }
        };
    }
}
