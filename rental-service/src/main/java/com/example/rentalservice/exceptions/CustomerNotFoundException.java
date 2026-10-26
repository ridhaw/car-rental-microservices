package com.example.rentalservice.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(Long id) {
        super("Client introuvable avec l'id : " + id);
    }
}
