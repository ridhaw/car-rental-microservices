package com.ecommerce.authservice.controller;
import com.ecommerce.authservice.dto.AuthRequest;
import com.ecommerce.authservice.dto.AuthResponse;
import com.ecommerce.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @GetMapping("/public/ping")
    public String ping() {
        return "auth-service OK";
    }
}