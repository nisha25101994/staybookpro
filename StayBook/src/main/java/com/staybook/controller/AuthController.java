package com.staybook.controller;

import com.staybook.entity.Role;

import com.staybook.entity.User;
import com.staybook.repository.UserRepository;
import com.staybook.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository repo,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {

        System.out.println("===== LOGIN DEBUG =====");
        System.out.println("EMAIL RECEIVED = " + user.getEmail());
        System.out.println("PASSWORD RECEIVED = " + user.getPassword());

        String email = user.getEmail().trim().toLowerCase();

        User dbUser = repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email"));

        if (!encoder.matches(user.getPassword(), dbUser.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(
                dbUser.getEmail(),
                dbUser.getRole()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", dbUser.getRole());

        return response;
    }


    @PostMapping("/register")
    public User register(@RequestBody User user) {

        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);

        return repo.save(user);
    }

}
