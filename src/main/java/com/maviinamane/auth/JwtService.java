package com.maviinamane.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${app.jwt-secret}") String secret) {
        try {
            key = Keys.hmacShaKeyFor(MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception error) {
            throw new IllegalStateException(error);
        }
    }

    public String create(String email) {
        return create(email, "ADMIN");
    }

    public String create(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(28800)))
                .signWith(key)
                .compact();
    }

    public String email(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String role(String token) {
        Object role = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("role");
        return role == null ? "ADMIN" : role.toString();
    }
}
