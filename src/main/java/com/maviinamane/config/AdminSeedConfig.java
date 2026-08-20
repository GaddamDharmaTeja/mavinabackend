package com.maviinamane.config;
import com.maviinamane.auth.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class AdminSeedConfig {

    @Bean
    CommandLineRunner seedAdmin(
            AdminUserRepository users,
            PasswordEncoder encoder,
            @Value("${ADMIN_EMAIL:}") String email,
            @Value("${ADMIN_PASSWORD:}") String password) {

        return args -> {
            if (!email.isBlank() && !password.isBlank() && users.findByEmailIgnoreCase(email).isEmpty()) {
                AdminUser user = new AdminUser();
                user.setEmail(email.trim());
                user.setPasswordHash(encoder.encode(password));
                users.save(user);
            }
        };
    }
}