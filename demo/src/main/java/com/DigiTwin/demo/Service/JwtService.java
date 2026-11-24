package com.DigiTwin.demo.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
        @Value("${app.jwt.secret:HIABUDHUACKJXBCJBAWUOH@EY!*YE*#YRG}") String secret,
        @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generate(String userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder().subject(userId).claim("email", email).claim("role", role)
            .issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(expirationMs)))
            .signWith(key).compact();
    }

    public io.jsonwebtoken.Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
