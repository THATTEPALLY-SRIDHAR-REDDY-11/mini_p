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
@Table(name = "donations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

    @Id
    @UuidGenerator
    private UUID id;

    private String foodName;

    private int quantity;

    private String location;

    private String status;

    private UUID restaurantId;

    private LocalDateTime createdAt;
}
