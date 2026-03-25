package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.model.Driver;
import com.example.demo.repository.DriverRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Driver addDriver(Driver driver) {
        driver.setStatus("AVAILABLE");
        return driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(UUID driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new IllegalArgumentException("Driver not found: " + driverId));
    }

    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }
}
