package com.example.demo.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pickup_requests")
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID donationId;

    private UUID ngoId;

    private UUID driverId;

    private String status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDonationId() {
        return donationId;
    }

    public void setDonationId(UUID donationId) {
        this.donationId = donationId;
    }

    public UUID getNgoId() {
        return ngoId;
    }

    public void setNgoId(UUID ngoId) {
        this.ngoId = ngoId;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
