package com.example.carservice.repositories;

import com.example.carservice.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByBrandIgnoreCase(String brand);
    List<Car> findByAvailable(boolean available);
    Optional<Car> findByRegistrationIgnoreCase(String registration);

    List<Car> findByPricePerDayBetween(double min, double max);

    List<Car> findByColorIgnoreCase(String color);

    List<Car> findByYear(Integer year);
    List<Car> findByYearBetween(Integer startYear, Integer endYear);

    List<Car> findByColorIgnoreCaseAndAvailable(String color, boolean available);

    @Query("SELECT c FROM Car c WHERE " +
            "LOWER(c.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.registration) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.color) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Car> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c.color, COUNT(c) FROM Car c WHERE c.color IS NOT NULL GROUP BY c.color ORDER BY COUNT(c) DESC")
    List<Object[]> countCarsByColor();


}