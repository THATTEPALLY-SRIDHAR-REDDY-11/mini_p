package com.example.demo.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.model.Donation;
import com.example.demo.repository.DonationRepository;

@Service
public class DonationService {

    private final DonationRepository donationRepository;

    public DonationService(DonationRepository donationRepository) {
        this.donationRepository = donationRepository;
    }

    public Donation addDonation(Donation donation) {
        donation.setStatus("AVAILABLE");
        return donationRepository.save(donation);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Donation getDonationById(UUID donationId) {
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found: " + donationId));
    }

    public Donation save(Donation donation) {
        return donationRepository.save(donation);
    }
}
