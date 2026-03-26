package com.example.demo.model;

import java.util.UUID;

import lombok.Data;

@Data
public class AISearchRequest {
    private String message;
    private String role;
    private UUID userId;
}
