package com.example.rentalservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class RentalRequest {
    private Long carId;
    private Long customerId;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructeur par défaut
    public RentalRequest() {}

    // Constructeur avec paramètres pour supporter les deux formats JSON
    @JsonCreator
    public RentalRequest(
            @JsonProperty("carId") Long carId,
            @JsonProperty("customerId") Long customerId,
            @JsonProperty("customer") CustomerWrapper customerWrapper,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("endDate") LocalDate endDate) {
        this.carId = carId;
        // Support both formats: direct customerId OR customer object with id
        if (customerId != null) {
            this.customerId = customerId;
        } else if (customerWrapper != null && customerWrapper.getId() != null) {
            this.customerId = customerWrapper.getId();
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters et Setters
    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Classe interne pour wrapper l'objet customer
    public static class CustomerWrapper {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    @Override
    public String toString() {
        return "RentalRequest{" +
                "carId=" + carId +
                ", customerId=" + customerId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}