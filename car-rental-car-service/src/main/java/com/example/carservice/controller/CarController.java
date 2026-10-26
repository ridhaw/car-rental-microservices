package com.example.carservice.controller;

import com.example.carservice.dto.BulkAvailabilityRequest;
import com.example.carservice.dto.CarStatsDTO;
import com.example.carservice.exceptions.CarNotFoundException;
import com.example.carservice.models.Car;
import com.example.carservice.repositories.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car API", description = "Gestion des voitures")
public class CarController {

    @Autowired
    private CarRepository carRepository;

    // ─── LECTURE — USER et ADMIN ──────────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Lister toutes les voitures avec tri optionnel")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Car> listCars(
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return carRepository.findAll(sort);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une voiture par ID")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        return ResponseEntity.ok(car);
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche full-text (marque, modèle, immatriculation, couleur)")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Car> search(
            @RequestParam @Parameter(description = "Mot-clé de recherche") String keyword) {
        return carRepository.searchByKeyword(keyword);
    }

    @GetMapping("/brand/{brand}")
    @Operation(summary = "Rechercher par marque")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Car>> getCarsByBrand(@PathVariable String brand) {
        List<Car> cars = carRepository.findByBrandIgnoreCase(brand);
        if (cars.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/registration/{registration}")
    @Operation(summary = "Rechercher par immatriculation")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Car> getCarByRegistration(@PathVariable String registration) {
        return carRepository.findByRegistrationIgnoreCase(registration)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/price-range")
    @Operation(summary = "Filtrer par fourchette de prix")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Car>> getCarsByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        if (min > max) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(carRepository.findByPricePerDayBetween(min, max));
    }

    @GetMapping("/color/{color}")
    @Operation(summary = "Rechercher les voitures par couleur")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> cars = carRepository.findByColorIgnoreCase(color);
        if (cars.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/color/{color}/available")
    @Operation(summary = "Voitures disponibles d'une couleur donnée")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public List<Car> getAvailableCarsByColor(@PathVariable String color) {
        return carRepository.findByColorIgnoreCaseAndAvailable(color, true);
    }

    @GetMapping("/year/{year}")
    @Operation(summary = "Rechercher les voitures par année de fabrication")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Car>> getCarsByYear(@PathVariable Integer year) {
        List<Car> cars = carRepository.findByYear(year);
        if (cars.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/year-range")
    @Operation(summary = "Voitures fabriquées entre deux années")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<Car>> getCarsByYearRange(
            @RequestParam Integer startYear,
            @RequestParam Integer endYear) {
        if (startYear > endYear) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(carRepository.findByYearBetween(startYear, endYear));
    }

    @GetMapping("/colors/popular")
    @Operation(summary = "Couleur la plus populaire du parc")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> getMostPopularColor() {
        List<Object[]> results = carRepository.countCarsByColor();
        if (results.isEmpty()) return ResponseEntity.noContent().build();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("mostPopularColor", results.get(0)[0]);
        response.put("count", results.get(0)[1]);

        Map<String, Long> allColors = new LinkedHashMap<>();
        for (Object[] row : results) {
            allColors.put((String) row[0], (Long) row[1]);
        }
        response.put("allColors", allColors);

        return ResponseEntity.ok(response);
    }

    // ─── ÉCRITURE — ADMIN uniquement ─────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Créer une voiture")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car savedCar = carRepository.save(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une voiture")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car newCar) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));

        car.setRegistration(newCar.getRegistration());
        car.setAvailable(newCar.isAvailable());
        car.setBrand(newCar.getBrand());
        car.setModel(newCar.getModel());
        car.setPricePerDay(newCar.getPricePerDay());
        car.setColor(newCar.getColor());
        car.setYear(newCar.getYear());

        return ResponseEntity.ok(carRepository.save(car));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une voiture")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        carRepository.delete(car);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Modifier la disponibilité d'une voiture")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Car> updateAvailability(@PathVariable Long id,
                                                  @RequestParam boolean value) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new CarNotFoundException(id));
        car.setAvailable(value);
        return ResponseEntity.ok(carRepository.save(car));
    }

    @DeleteMapping("/bulk")
    @Operation(summary = "Supprimer plusieurs voitures par leurs IDs")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkDelete(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) return ResponseEntity.badRequest().build();

        List<Car> carsToDelete = carRepository.findAllById(ids);
        carRepository.deleteAll(carsToDelete);

        Map<String, Object> response = new HashMap<>();
        response.put("deletedCount", carsToDelete.size());
        response.put("ids", ids);

        return ResponseEntity.ok(response);
    }
}