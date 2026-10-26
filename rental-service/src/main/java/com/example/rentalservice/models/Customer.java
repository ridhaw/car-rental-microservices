package com.example.rentalservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String phone;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Rental> rentals;

    public Customer() {}

    public Customer(Long id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Rental> getRentals() { return rentals; }
    public void setRentals(List<Rental> rentals) { this.rentals = rentals; }
}
