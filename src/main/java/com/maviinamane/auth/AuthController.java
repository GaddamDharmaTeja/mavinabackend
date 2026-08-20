package com.maviinamane.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "${app.cors-origin:http://localhost:3000}")
public class AuthController {

    private final AdminUserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(AdminUserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/login")
    public Token login(@RequestBody Login request) {
        AdminUser user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return new Token(jwt.create(user.getEmail(), user.getRole()), user.getEmail());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout() {
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Token register(@Valid @RequestBody Register request) {
        if (users.count() > 0) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin registration is closed. Sign in with an existing administrator account.");
        }
        AdminUser user = new AdminUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPhone(request.phone().trim());
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole("ADMIN");
        users.save(user);
        return new Token(jwt.create(user.getEmail(), user.getRole()), user.getEmail());
    }

    public record Login(@Email @NotBlank String email, @NotBlank String password) {
    }
    public record Register(@NotBlank String fullName, @Email @NotBlank String email, @NotBlank String phone,
                           @jakarta.validation.constraints.Size(min = 8) String password) {
    }

    public record Token(String token, String email) {
    }
}
