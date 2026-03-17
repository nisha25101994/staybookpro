package com.assettrackpro.security;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.assettrackpro.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil  {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24h

    private final Key key =
            Keys.hmacShaKeyFor("assettrackpro-secret-key-1234567890".getBytes());

    public String generateToken(String email, Role role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // ✅ FIXED HERE
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {

        String role = getClaims(token).get("role", String.class);

        // Do NOT add ROLE_ prefix because controllers use hasAuthority()
        return List.of(new SimpleGrantedAuthority(role));
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}