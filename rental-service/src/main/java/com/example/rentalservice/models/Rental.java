package com.example.rentalservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long carId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties({"rentals", "hibernateLazyInitializer"})
    private Customer customer;

    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RentalStatus status = RentalStatus.PENDING;

    public Rental() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }
}
