package com.example.rentalservice.repositories;

import com.example.rentalservice.models.Rental;
import com.example.rentalservice.models.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    @Query("SELECT r FROM Rental r WHERE r.customer.id = :customerId")
    List<Rental> findByCustomerId(@Param("customerId") Long customerId);

    List<Rental> findByStatus(RentalStatus status);

    @Query("SELECT r FROM Rental r WHERE r.startDate >= :start AND r.endDate <= :end")
    List<Rental> findByDateRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT r FROM Rental r WHERE " +
            "(:customerName IS NULL OR LOWER(r.customer.name) LIKE LOWER(CONCAT('%', :customerName, '%'))) AND " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:minPrice IS NULL OR r.totalPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR r.totalPrice <= :maxPrice) AND " +
            "(:startDate IS NULL OR r.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR r.startDate <= :endDate)")
    List<Rental> searchRentals(
            @Param("customerName") String customerName,
            @Param("status") RentalStatus status,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM Rental r WHERE r.status = :status AND r.endDate < :date")
    List<Rental> findByStatusAndEndDateBefore(
            @Param("status") RentalStatus status,
            @Param("date") LocalDate date
    );

    // Vérifie si une voiture est déjà réservée sur la période demandée
    @Query("SELECT r FROM Rental r WHERE r.carId = :carId " +
            "AND r.status IN ('PENDING', 'ACTIVE') " +
            "AND r.startDate < :endDate " +
            "AND r.endDate > :startDate")
    List<Rental> findConflictingRentals(
            @Param("carId") Long carId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Rentals qui commencent aujourd'hui
    @Query("SELECT r FROM Rental r WHERE r.status = 'PENDING' AND r.startDate = :today")
    List<Rental> findRentalsStartingToday(@Param("today") LocalDate today);

    // Rentals qui se terminent avant aujourd'hui
    @Query("SELECT r FROM Rental r WHERE r.status = 'ACTIVE' AND r.endDate < :today")
    List<Rental> findExpiredRentals(@Param("today") LocalDate today);
}
