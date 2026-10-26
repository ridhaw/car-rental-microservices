package com.example.rentalservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(new String(keyBytes)));
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) { return getClaims(token).getSubject(); }
    public String extractRole(String token) { return getClaims(token).get("role", String.class); }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
    }
}
