package com.example.carservice.dto;

import java.util.Map;

public class CarStatsDTO {

    private long totalCars;
    private long availableCars;
    private long unavailableCars;
    private double availabilityRate;
    private double averagePricePerDay;
    private double minPrice;
    private double maxPrice;
    private Map<String, Long> carsByBrand;
    private Map<String, Long> carsByColor;
    private String mostPopularColor;

    public CarStatsDTO() {}

    public long getTotalCars() { return totalCars; }
    public void setTotalCars(long totalCars) { this.totalCars = totalCars; }

    public long getAvailableCars() { return availableCars; }
    public void setAvailableCars(long availableCars) { this.availableCars = availableCars; }

    public long getUnavailableCars() { return unavailableCars; }
    public void setUnavailableCars(long unavailableCars) { this.unavailableCars = unavailableCars; }

    public double getAvailabilityRate() { return availabilityRate; }
    public void setAvailabilityRate(double availabilityRate) { this.availabilityRate = availabilityRate; }

    public double getAveragePricePerDay() { return averagePricePerDay; }
    public void setAveragePricePerDay(double averagePricePerDay) { this.averagePricePerDay = averagePricePerDay; }

    public double getMinPrice() { return minPrice; }
    public void setMinPrice(double minPrice) { this.minPrice = minPrice; }

    public double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }

    public Map<String, Long> getCarsByBrand() { return carsByBrand; }
    public void setCarsByBrand(Map<String, Long> carsByBrand) { this.carsByBrand = carsByBrand; }

    public Map<String, Long> getCarsByColor() { return carsByColor; }
    public void setCarsByColor(Map<String, Long> carsByColor) { this.carsByColor = carsByColor; }

    public String getMostPopularColor() { return mostPopularColor; }
    public void setMostPopularColor(String mostPopularColor) { this.mostPopularColor = mostPopularColor; }
}
