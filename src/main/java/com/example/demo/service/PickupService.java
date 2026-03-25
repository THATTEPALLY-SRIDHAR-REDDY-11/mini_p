package com.example.demo.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Donation;
import com.example.demo.model.Driver;
import com.example.demo.model.PickupRequest;
import com.example.demo.repository.PickupRepository;

@Service
public class PickupService {

    private final PickupRepository pickupRepository;
    private final DonationService donationService;
    private final DriverService driverService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PickupService(PickupRepository pickupRepository, DonationService donationService, DriverService driverService) {
        this.pickupRepository = pickupRepository;
        this.donationService = donationService;
        this.driverService = driverService;
    }

    @Transactional
    public PickupRequest requestPickup(PickupRequest pickupRequest) {
        pickupRequest.setStatus("REQUESTED");
        pickupRequest.setDriverId(null);

        Donation donation = donationService.getDonationById(pickupRequest.getDonationId());
        donation.setStatus("REQUESTED");
        donationService.save(donation);

        return pickupRepository.save(pickupRequest);
    }

    public List<PickupRequest> getAllPickups() {
        return pickupRepository.findAll();
    }

    public List<PickupRequest> getPickupsByDriver(UUID driverId) {
        return pickupRepository.findByDriverId(driverId);
    }

    @Transactional
    public PickupRequest assignDriver(UUID pickupId, UUID driverId) {
        PickupRequest pickup = getPickupById(pickupId);
        Driver driver = driverService.getDriverById(driverId);

        pickup.setDriverId(driverId);
        pickup.setStatus("ASSIGNED");

        driver.setStatus("ASSIGNED");
        driverService.save(driver);

        Donation donation = donationService.getDonationById(pickup.getDonationId());
        donation.setStatus("ASSIGNED");
        donationService.save(donation);

        return pickupRepository.save(pickup);
    }

    @Transactional
    public PickupRequest collectFood(UUID pickupId) {
        PickupRequest pickup = getPickupById(pickupId);
        pickup.setStatus("COLLECTED");
        PickupRequest savedPickup = pickupRepository.save(pickup);

        Donation donation = donationService.getDonationById(savedPickup.getDonationId());
        donation.setStatus("COLLECTED");
        donationService.save(donation);

        scheduleDeliveryCompletion(savedPickup.getId());
        return savedPickup;
    }

    private void scheduleDeliveryCompletion(UUID pickupId) {
        scheduler.schedule(() -> completeDelivery(pickupId), 1, TimeUnit.MINUTES);
    }

    @Transactional
    public void completeDelivery(UUID pickupId) {
        PickupRequest pickup = pickupRepository.findById(pickupId).orElse(null);
        if (pickup == null || !"COLLECTED".equalsIgnoreCase(pickup.getStatus())) {
            return;
        }

        pickup.setStatus("DELIVERED");
        pickupRepository.save(pickup);

        Donation donation = donationService.getDonationById(pickup.getDonationId());
        donation.setStatus("DELIVERED");
        donationService.save(donation);

        if (pickup.getDriverId() != null) {
            Driver driver = driverService.getDriverById(pickup.getDriverId());
            driver.setStatus("AVAILABLE");
            driverService.save(driver);
        }
    }

    public PickupRequest getPickupById(UUID pickupId) {
        return pickupRepository.findById(pickupId)
                .orElseThrow(() -> new IllegalArgumentException("Pickup request not found: " + pickupId));
    }
}
