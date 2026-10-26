package com.example.carservice.exceptions;

public class CarNotAvailableException extends RuntimeException {
    public CarNotAvailableException(Long id) {
        super("La voiture avec l'id " + id + " n'est pas disponible.");
    }
}
