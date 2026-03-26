package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Donation;
import com.example.demo.service.DonationService;

@RestController
@RequestMapping("/donations")
public class DonationController {

    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public Donation addDonation(@RequestBody Donation donation) {
        return donationService.addDonation(donation);
    }

    @GetMapping
    public List<Donation> getAllDonations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) UUID restaurantId) {
        return donationService.getAllDonations(status, date, restaurantId);
    }
}
