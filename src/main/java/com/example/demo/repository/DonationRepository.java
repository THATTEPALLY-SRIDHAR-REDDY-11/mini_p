package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

    List<Donation> findByStatus(String status);

    List<Donation> findByRestaurantId(UUID restaurantId);
}
