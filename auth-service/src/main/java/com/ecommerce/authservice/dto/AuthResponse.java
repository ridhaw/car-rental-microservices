package com.ecommerce.authservice.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private Long customerId;

    public AuthResponse(String token, String username,
                        String role, Long customerId) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.customerId = customerId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public Long getCustomerId() { return customerId; }
}