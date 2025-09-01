package com.mazen.ecommerce.auth.repository;

import com.mazen.ecommerce.auth.model.User;
import com.mazen.ecommerce.auth.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsEnabledTrue(String email);

    List<User> findByRole(Role role);

    List<User> findByIsEnabledTrue();

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findUsersCreatedSince(java.time.LocalDateTime since);
}
