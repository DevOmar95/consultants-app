package com.example.consultants_api.controller;

import com.example.consultants_api.dto.LoginRequest;
import com.example.consultants_api.dto.LoginResponse;
import com.example.consultants_api.model.AppUser;
import com.example.consultants_api.repository.UserRepository;
import com.example.consultants_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtUtil.generate(request.getUsername());
        return ResponseEntity.ok(new LoginResponse(token, request.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest request) {
        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return ResponseEntity.ok("User created");
    }
}