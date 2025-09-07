
// ================================
// EMAIL SERVICE (PLACEHOLDER FOR FUTURE)
// ================================


/**
 * Placeholder for email notifications
 * Ready for welcome emails, password resets, etc.
 * Currently logs instead of sending
 */

package com.mazen.ecommerce.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendWelcomeEmail(String email, String name) {
        // TODO: Implement email sending
        log.info("Would send welcome email to: {} ({})", email, name);
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        // TODO: Implement password reset email
        log.info("Would send password reset email to: {}", email);
    }

    public void sendPasswordChangedNotification(String email) {
        // TODO: Implement password change notification
        log.info("Would send password changed notification to: {}", email);
    }

    public void sendAccountStatusChangeNotification(String email, boolean enabled) {
        // TODO: Implement account status change notification
        log.info("Would send account {} notification to: {}",
                enabled ? "enabled" : "disabled", email);
    }
}
