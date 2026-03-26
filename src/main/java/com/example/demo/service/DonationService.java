package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        donation.setCreatedAt(LocalDateTime.now());
        return donationRepository.save(donation);
    }

    public List<Donation> getAllDonations(String status, LocalDate date, UUID restaurantId) {
        List<Donation> source = restaurantId == null
                ? donationRepository.findAll()
                : donationRepository.findByRestaurantId(restaurantId);

        return source.stream()
                .filter(d -> status == null || status.isBlank() || status.equalsIgnoreCase(d.getStatus()))
                .filter(d -> date == null || (d.getCreatedAt() != null && d.getCreatedAt().toLocalDate().equals(date)))
                .sorted((a, b) -> {
                    LocalDateTime aTime = a.getCreatedAt() == null ? LocalDateTime.MIN : a.getCreatedAt();
                    LocalDateTime bTime = b.getCreatedAt() == null ? LocalDateTime.MIN : b.getCreatedAt();
                    return bTime.compareTo(aTime);
                })
                .collect(Collectors.toList());
    }

    public Donation getDonationById(UUID donationId) {
        return donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found: " + donationId));
    }

    public Donation save(Donation donation) {
        return donationRepository.save(donation);
    }
}
