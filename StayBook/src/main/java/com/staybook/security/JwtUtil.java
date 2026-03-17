package com.staybook.security;

import com.staybook.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24h

    private final Key key =
            Keys.hmacShaKeyFor(
                    "staybook-secret-key-staybook-1234567890".getBytes()
            );

    // ✅ TOKEN GENERATION
    public String generateToken(String email, Role role) {

        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())   // ADMIN / VENDOR / USER
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )
                .signWith(key)
                .compact();
    }

    // ✅ EXTRACT USERNAME
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ EXTRACT AUTHORITIES (FIXED HERE)
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {

        String role = getClaims(token).get("role", String.class);

        // 🔥 IMPORTANT FIX: REMOVE "ROLE_"
        return List.of(new SimpleGrantedAuthority(role));
    }

    // ✅ VALIDATE TOKEN
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("JWT ERROR: " + e.getMessage());
            return false;
        }
    }

    // 🔒 INTERNAL CLAIM PARSER
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}