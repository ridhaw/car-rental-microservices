package com.example.rentalservice.controller;

import com.example.rentalservice.dto.RentalRequest;
import com.example.rentalservice.models.Rental;
import com.example.rentalservice.models.RentalStatus;
import com.example.rentalservice.repositories.RentalRepository;
import com.example.rentalservice.services.RentalScheduler;
import com.example.rentalservice.services.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rental API", description = "Gestion des locations de voitures")
public class RentalController {

    @Autowired
    private RentalService rentalService;
    @Autowired
    private RentalRepository rentalRepository;

    @PostMapping
    @Operation(summary = "Créer une nouvelle location (vérifie la disponibilité)")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<Rental> createRental(@RequestBody RentalRequest request) {
        Rental rental = rentalService.createRental(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rental);
    }

    @GetMapping
    @Operation(summary = "Lister toutes les locations")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une location par ID")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.getRentalById(id));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Historique des locations d'un client")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Rental>> getRentalsByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(rentalService.getRentalsByCustomer(customerId));
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Clôturer une location et libérer la voiture")
    public ResponseEntity<Rental> closeRental(@PathVariable Long id) {
        return ResponseEntity.ok(rentalService.closeRental(id));
    }

    @GetMapping("/active")
    @Operation(summary = "Lister les locations actives")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Rental>> getActiveRentals() {
        return ResponseEntity.ok(rentalRepository.findByStatus(RentalStatus.ACTIVE));
    }

    @GetMapping("/date")
    @Operation(summary = "Filtrer les locations par période")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Rental>> getRentalsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(rentalService.getRentalsByDateRange(start, end));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @Operation(summary = "Recherche avancée des locations",
            description = "Filtres combinés : nom client, status, prix, dates. Tous les paramètres sont optionnels.")
    public ResponseEntity<List<Rental>> searchRentals(
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(rentalService.searchRentals(
                customerName, status, minPrice, maxPrice, startDate, endDate));
    }


    @Autowired
    private RentalScheduler rentalScheduler;

    @PostMapping("/scheduler/run")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(summary = "Déclencher manuellement la vérification des locations expirées")
    public ResponseEntity<String> runScheduler() {
        rentalScheduler.closeExpiredRentals();
        return ResponseEntity.ok("Scheduler exécuté avec succès");
    }
}
