package com.example.carservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String model;
    private String brand;
    private String registration;
    private boolean available;
    private double pricePerDay;
    private String color;
    private Integer year;

    public Car(Long id, String model, String brand, String registration,
               boolean available, double pricePerDay, String color, Integer year) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.registration = registration;
        this.available = available;
        this.pricePerDay = pricePerDay;
        this.color = color;
        this.year = year;
    }

    public Car() {}

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

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}