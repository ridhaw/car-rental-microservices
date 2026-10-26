package com.example.rentalservice.exceptions;

public class CarUnavailableException extends RuntimeException {
    public CarUnavailableException(Long carId) {
        super("La voiture avec l'id " + carId + " est indisponible à la location.");
    }
}
