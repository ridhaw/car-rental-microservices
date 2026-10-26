package com.ecommerce.authservice.service;

import com.ecommerce.authservice.dto.AuthRequest;
import com.ecommerce.authservice.dto.AuthResponse;
import com.ecommerce.authservice.dto.CustomerRequest;
import com.ecommerce.authservice.dto.CustomerResponse;
import com.ecommerce.authservice.feign.CustomerFeignClient;
import com.ecommerce.authservice.model.User;
import com.ecommerce.authservice.repository.UserRepository;
import com.ecommerce.authservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerFeignClient customerFeignClient;

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(),user.getCustomerId());
    }

    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Nom d'utilisateur déjà pris");
        }

        CustomerResponse customer = customerFeignClient.createCustomer(
                new CustomerRequest(request.getName(), request.getPhone())
        );


        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCustomerId(customer.getId());
        if(request.getRole() == null || request.getRole().isBlank()){
            user.setRole("ROLE_USER");
        }else{
            user.setRole(request.getRole());
        }

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getRole(),customer.getId());
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }
}