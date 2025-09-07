
// ================================
// AUDIT SERVICE (FOR SECURITY EVENTS)
// ================================

package com.mazen.ecommerce.auth.service;

import com.mazen.ecommerce.auth.model.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditService {

    public void logUserRegistration(String email, Role role) {
        log.info("[AUDIT] User registered - Email: {}, Role: {}, Time: {}",
                email, role, LocalDateTime.now());
    }

    public void logUserLogin(String email, String ipAddress) {
        log.info("[AUDIT] User login - Email: {}, IP: {}, Time: {}",
                email, ipAddress, LocalDateTime.now());
    }

    public void logUserLogout(String email) {
        log.info("[AUDIT] User logout - Email: {}, Time: {}",
                email, LocalDateTime.now());
    }

    public void logPasswordChange(String email) {
        log.info("[AUDIT] Password changed - Email: {}, Time: {}",
                email, LocalDateTime.now());
    }

    public void logRoleChange(String email, Role oldRole, Role newRole, String adminEmail) {
        log.info("[AUDIT] Role changed - User: {}, Old Role: {}, New Role: {}, Changed by: {}, Time: {}",
                email, oldRole, newRole, adminEmail, LocalDateTime.now());
    }

    public void logAccountStatusChange(String email, boolean enabled, String adminEmail) {
        log.info("[AUDIT] Account status changed - User: {}, Status: {}, Changed by: {}, Time: {}",
                email, enabled ? "ENABLED" : "DISABLED", adminEmail, LocalDateTime.now());
    }

    public void logFailedLogin(String email, String reason, String ipAddress) {
        log.warn("[AUDIT] Failed login attempt - Email: {}, Reason: {}, IP: {}, Time: {}",
                email, reason, ipAddress, LocalDateTime.now());
    }

    public void logTokenValidation(boolean success, String email) {
        log.debug("[AUDIT] Token validation - Success: {}, Email: {}, Time: {}",
                success, email, LocalDateTime.now());
    }
}