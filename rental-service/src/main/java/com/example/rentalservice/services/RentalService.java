package com.example.rentalservice.services;

import com.example.rentalservice.dto.CarDTO;
import com.example.rentalservice.dto.RentalRequest;
import com.example.rentalservice.exceptions.CarUnavailableException;
import com.example.rentalservice.exceptions.CustomerNotFoundException;
import com.example.rentalservice.exceptions.RentalNotFoundException;
import com.example.rentalservice.feign.CarFeignClient;
import com.example.rentalservice.models.Customer;
import com.example.rentalservice.models.Rental;
import com.example.rentalservice.models.RentalStatus;
import com.example.rentalservice.repositories.CustomerRepository;
import com.example.rentalservice.repositories.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CarFeignClient carFeignClient;

    // ── Rentals ────────────────────────────────────────────────────────────

    /*public Rental createRental(RentalRequest request) {
        // 1. Vérifier que le client existe
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        // 2. Vérifier la disponibilité via Car-Service (Feign)
        CarDTO car = carFeignClient.getCarById(request.getCarId());
        if (!car.isAvailable()) {
            throw new CarUnavailableException(request.getCarId());
        }

        // 3. Calcul du prix total : nbr_jours × pricePerDay
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            throw new IllegalStateException("La date de fin doit être postérieure à la date de début.");
        }
        double totalPrice = days * car.getPricePerDay();

        // 4. Marquer la voiture comme indisponible
        carFeignClient.updateAvailability(request.getCarId(), false);

        // 5. Créer et sauvegarder la location
        Rental rental = new Rental();
        rental.setCarId(request.getCarId());
        rental.setCustomer(customer);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalPrice(totalPrice);
        rental.setStatus(RentalStatus.ACTIVE);

        return rentalRepository.save(rental);
    }*/
    public Rental createRental(RentalRequest request) {
        // 1. Vérifier que le client existe
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(request.getCustomerId()));

        // 2. Récupérer la voiture
        CarDTO car = carFeignClient.getCarById(request.getCarId());

        // 3. Vérifier que la voiture n'est pas déjà réservée sur cette période
        List<Rental> conflits = rentalRepository.findConflictingRentals(
                request.getCarId(),
                request.getStartDate(),
                request.getEndDate()
        );
        if (!conflits.isEmpty()) {
            throw new CarUnavailableException(request.getCarId());
        }

        // 4. Validation des dates
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            throw new IllegalStateException("La date de fin doit être postérieure à la date de début.");
        }

        // 5. Calcul du prix
        double totalPrice = days * car.getPricePerDay();

        // 6. ❌ Ne plus appeler carFeignClient.updateAvailability() ici
        //    La voiture reste disponible jusqu'au startDate

        // 7. Créer le rental avec status PENDING si date future, ACTIVE si aujourd'hui
        Rental rental = new Rental();
        rental.setCarId(request.getCarId());
        rental.setCustomer(customer);
        rental.setStartDate(request.getStartDate());
        rental.setEndDate(request.getEndDate());
        rental.setTotalPrice(totalPrice);

        // Status selon la date
        if (request.getStartDate().isEqual(LocalDate.now())) {
            rental.setStatus(RentalStatus.ACTIVE);
            carFeignClient.updateAvailability(request.getCarId(), false); // démarre aujourd'hui
        } else {
            rental.setStatus(RentalStatus.PENDING); // date future
        }

        return rentalRepository.save(rental);
    }

    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    public List<Rental> getRentalsByCustomer(Long customerId) {
        // Vérifier que le client existe
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return rentalRepository.findByCustomerId(customerId);
    }

    public Rental closeRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (rental.getStatus() == RentalStatus.CLOSED) {
            throw new IllegalStateException("La location est déjà clôturée.");
        }

        // Libérer la voiture via Car-Service (Feign)
        carFeignClient.updateAvailability(rental.getCarId(), true);

        rental.setStatus(RentalStatus.CLOSED);
        return rentalRepository.save(rental);
    }

    public Rental getRentalById(Long id) {
        return rentalRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));
    }

    public List<Rental> getRentalsByDateRange(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalStateException("La date de début doit être antérieure à la date de fin.");
        }
        return rentalRepository.findByDateRange(start, end);
    }

    public List<Rental> searchRentals(String customerName,
                                      String status,
                                      Double minPrice,
                                      Double maxPrice,
                                      LocalDate startDate,
                                      LocalDate endDate) {

        // Convertir le status string en enum (null = tous)
        RentalStatus rentalStatus = null;
        if (status != null && !status.equalsIgnoreCase("all")) {
            try {
                rentalStatus = RentalStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Status invalide. Valeurs acceptées : ACTIVE, CLOSED, ALL");
            }
        }

        // Date de fin par défaut = aujourd'hui si non fournie
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        // Validation prix
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalStateException("Le prix minimum ne peut pas être supérieur au prix maximum.");
        }

        return rentalRepository.searchRentals(
                customerName,
                rentalStatus,
                minPrice,
                maxPrice,
                startDate,
                endDate
        );
    }

    // ── Customers ──────────────────────────────────────────────────────────

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updated) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        return customerRepository.save(existing);
    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        customerRepository.delete(customer);
    }
}
