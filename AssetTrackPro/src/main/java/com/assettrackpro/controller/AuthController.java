package com.assettrackpro.controller;

import com.assettrackpro.entity.User;
import com.assettrackpro.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null) {
            email = request.get("username");
        }
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email/Username and Password are required"));
        }

        try {
            Map<String, Object> response = authService.login(email, password);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String errorMsg = e.getMessage();
            if ("Invalid password".equals(errorMsg) || "User not found".equals(errorMsg)) {
                // Keep it slightly descriptive for the user during this fix phase
                return ResponseEntity.status(401).body(Map.of("error", errorMsg));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Login failed: " + errorMsg));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getEmail() == null || user.getPassword() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Email and Password are required"));
        }

        try {
            authService.register(user);

            return ResponseEntity.ok(
                    Map.of("message", "Registration successful")
            );

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
