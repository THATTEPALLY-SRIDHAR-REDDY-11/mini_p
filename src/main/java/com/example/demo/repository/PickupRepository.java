package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.PickupRequest;

public interface PickupRepository extends JpaRepository<PickupRequest, UUID> {

    List<PickupRequest> findByDriverId(UUID driverId);

    List<PickupRequest> findByStatus(String status);
}
