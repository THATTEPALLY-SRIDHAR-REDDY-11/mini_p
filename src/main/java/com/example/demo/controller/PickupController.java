package com.example.demo.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.PickupRequest;
import com.example.demo.service.PickupService;

@RestController
@RequestMapping("/pickup")
public class PickupController {

    private final PickupService pickupService;

    public PickupController(PickupService pickupService) {
        this.pickupService = pickupService;
    }

    @PostMapping
    public PickupRequest requestPickup(@RequestBody PickupRequest pickupRequest) {
        return pickupService.requestPickup(pickupRequest);
    }

    @GetMapping
    public List<PickupRequest> getAllPickups() {
        return pickupService.getAllPickups();
    }

    @GetMapping("/driver/{driverId}")
    public List<PickupRequest> getPickupsByDriver(@PathVariable UUID driverId) {
        return pickupService.getPickupsByDriver(driverId);
    }

    @PutMapping("/assign/{pickupId}")
    public PickupRequest assignDriver(@PathVariable UUID pickupId, @RequestParam UUID driverId) {
        return pickupService.assignDriver(pickupId, driverId);
    }

    @PutMapping("/collect/{pickupId}")
    public PickupRequest collectFood(@PathVariable UUID pickupId) {
        return pickupService.collectFood(pickupId);
    }
}
