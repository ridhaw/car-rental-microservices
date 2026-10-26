package com.example.rentalservice.controller;

import com.example.rentalservice.models.Customer;
import com.example.rentalservice.repositories.CustomerRepository;
import com.example.rentalservice.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer API", description = "Gestion des clients")
public class CustomerController {

    @Autowired
    private RentalService rentalService;
    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    @Operation(summary = "Lister tous les clients")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(rentalService.getAllCustomers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un client par ID")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getCustomerById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un client")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rentalService.createCustomer(customer));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        return ResponseEntity.ok(rentalService.updateCustomer(id, customer));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        rentalService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher un client par nom")
    public ResponseEntity<List<Customer>> searchCustomers(@RequestParam String name) {
        return ResponseEntity.ok(customerRepository.findByNameContainingIgnoreCase(name));
    }
}
