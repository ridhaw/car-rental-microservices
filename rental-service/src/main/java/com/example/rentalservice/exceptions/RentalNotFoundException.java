package com.example.rentalservice.exceptions;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(Long id) {
        super("Location introuvable avec l'id : " + id);
    }
}
