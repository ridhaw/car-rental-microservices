package com.example.carservice.exceptions;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(Long id) {
        super("Voiture introuvable avec l'id : " + id);
    }
}
