package com.example.rentalservice.feign;

import com.example.rentalservice.dto.CarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "car-service")
public interface CarFeignClient {

    @GetMapping("/api/cars/{id}")
    CarDTO getCarById(@PathVariable("id") Long id);

    @PatchMapping("/api/cars/{id}/availability")
    CarDTO updateAvailability(@PathVariable("id") Long id, @RequestParam("value") boolean value);
}
