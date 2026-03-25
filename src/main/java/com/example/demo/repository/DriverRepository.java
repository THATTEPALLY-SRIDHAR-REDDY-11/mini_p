package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Driver;

public interface DriverRepository extends JpaRepository<Driver, UUID> {

    List<Driver> findByStatus(String status);
}
