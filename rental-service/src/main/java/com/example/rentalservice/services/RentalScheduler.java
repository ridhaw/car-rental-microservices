package com.example.rentalservice.services;

import com.example.rentalservice.feign.CarFeignClient;
import com.example.rentalservice.models.Rental;
import com.example.rentalservice.models.RentalStatus;
import com.example.rentalservice.repositories.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RentalScheduler {

    private static final Logger log = LoggerFactory.getLogger(RentalScheduler.class);

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarFeignClient carFeignClient;

    @Scheduled(cron = "0 0 0 * * *") // chaque jour à minuit
    public void processRentals() {
        LocalDate today = LocalDate.now();
        log.info("🕐 Scheduler démarré : {}", today);

        activateStartingRentals(today);
        closeExpiredRentals(today);
    }

    // 1. Activer les locations qui commencent aujourd'hui
    private void activateStartingRentals(LocalDate today) {
        List<Rental> startingToday = rentalRepository.findRentalsStartingToday(today);

        for (Rental rental : startingToday) {
            try {
                carFeignClient.updateAvailability(rental.getCarId(), false);
                rental.setStatus(RentalStatus.ACTIVE);
                rentalRepository.save(rental);
                log.info("🚗 Rental #{} activé - Voiture #{} indisponible",
                        rental.getId(), rental.getCarId());
            } catch (Exception e) {
                log.error("❌ Erreur activation rental #{} : {}", rental.getId(), e.getMessage());
            }
        }
    }

    // 2. Clôturer les locations expirées
    private void closeExpiredRentals(LocalDate today) {
        List<Rental> expiredRentals = rentalRepository.findExpiredRentals(today);

        for (Rental rental : expiredRentals) {
            try {
                carFeignClient.updateAvailability(rental.getCarId(), true);
                rental.setStatus(RentalStatus.CLOSED);
                rentalRepository.save(rental);
                log.info("✅ Rental #{} clôturé - Voiture #{} disponible",
                        rental.getId(), rental.getCarId());
            } catch (Exception e) {
                log.error("❌ Erreur clôture rental #{} : {}", rental.getId(), e.getMessage());
            }
        }
    }

    // Déclenchement manuel pour les tests
    public void closeExpiredRentals() {
        processRentals();
    }
}