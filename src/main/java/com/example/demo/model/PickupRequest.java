package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pickup_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupRequest {

    @Id
    @UuidGenerator
    private UUID id;

    private UUID donationId;

    private UUID ngoId;

    private UUID driverId;

    private String status;

    private LocalDateTime pickupTime;

    private LocalDateTime deliveryTime;
}
