package com.ecommerce.authservice.dto;

public class CustomerRequest {
    private String name;
    private String phone;

    public CustomerRequest(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
}
