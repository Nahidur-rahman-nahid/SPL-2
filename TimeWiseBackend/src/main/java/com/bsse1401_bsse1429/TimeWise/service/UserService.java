package com.bsse1401_bsse1429.TimeWise.service;

import com.bsse1401_bsse1429.TimeWise.model.User;
import com.bsse1401_bsse1429.TimeWise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserRepository repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // Register a new user and generate a token
    public ResponseEntity<?> register(User user) {
        // Check for duplicate username
        if (repo.findByUserName(user.getUserName()) != null) {
            return ResponseEntity.badRequest().body("Username already exists. Please choose another.");
        }

        // Check for duplicate email
        if (repo.findByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already registered. Please use another email.");
        }

        // Save the user in the database
        String rawPassword = user.getPassword();
        user.setPassword(encoder.encode(rawPassword));
        repo.save(user);

        // Log saved data
        System.out.println("User saved: " + user);

        // Authenticate and generate token
        user.setPassword(rawPassword); // Reset raw password for authentication
        String token = login(user);

        return ResponseEntity.ok(token);
    }

    public String login(User user) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword())
            );

            if (authentication.isAuthenticated()) {
                User authenticatedUser = repo.findByUserName(user.getUserName());
                if (authenticatedUser == null) {
                    throw new IllegalArgumentException("User not found in the database.");
                }

                return jwtService.generateToken(authenticatedUser.getUserId());
            } else {
                throw new IllegalArgumentException("Authentication failed. Invalid credentials.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Authentication failed. Bad credentials.", e);
        }
    }
}