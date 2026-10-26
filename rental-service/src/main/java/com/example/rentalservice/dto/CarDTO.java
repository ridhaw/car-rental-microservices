package com.example.rentalservice.dto;

public class CarDTO {
    private Long id;
    private String model;
    private String brand;
    private String registration;
    private boolean available;
    private double pricePerDay;

    public CarDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public double getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
}
