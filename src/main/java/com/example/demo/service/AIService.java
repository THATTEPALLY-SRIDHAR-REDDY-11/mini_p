package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.AISearchRequest;
import com.example.demo.model.AISearchResponse;
import com.example.demo.model.AISearchRow;
import com.example.demo.model.Donation;
import com.example.demo.model.PickupRequest;
import com.example.demo.repository.DonationRepository;
import com.example.demo.repository.PickupRepository;

@Service
public class AIService {

    private static final String TODAY_DONATIONS = "today_donations";
    private static final String AVAILABLE_DONATIONS = "available_donations";
    private static final String ASSIGNED_ORDERS = "assigned_orders";
    private static final String DELIVERED_ORDERS = "delivered_orders";
    private static final String MY_PICKUPS = "my_pickups";

    private final DonationRepository donationRepository;
    private final PickupRepository pickupRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${huggingface.api.url:https://api-inference.huggingface.co/models/google/flan-t5-base}")
    private String huggingFaceApiUrl;

    @Value("${huggingface.api.token:}")
    private String huggingFaceToken;

    public AIService(DonationRepository donationRepository, PickupRepository pickupRepository) {
        this.donationRepository = donationRepository;
        this.pickupRepository = pickupRepository;
    }

    public AISearchResponse search(AISearchRequest request) {
        String command = classifyCommand(request.getMessage());
        List<AISearchRow> rows = executeCommand(command, request.getUserId());

        if (rows.isEmpty()) {
            return AISearchResponse.builder()
                    .command(command)
                    .message("No matching records found")
                    .records(rows)
                    .build();
        }

        return AISearchResponse.builder()
                .command(command)
                .message("Records found")
                .records(rows)
                .build();
    }

    private List<AISearchRow> executeCommand(String command, UUID userId) {
        return switch (command) {
            case TODAY_DONATIONS -> getTodayDonations();
            case AVAILABLE_DONATIONS -> getAvailableDonations();
            case ASSIGNED_ORDERS -> getPickupsByStatus("ASSIGNED");
            case DELIVERED_ORDERS -> getPickupsByStatus("DELIVERED");
            case MY_PICKUPS -> getMyPickups(userId);
            default -> new ArrayList<>();
        };
    }

    private List<AISearchRow> getTodayDonations() {
        LocalDate today = LocalDate.now();
        return donationRepository.findAll().stream()
                .filter(d -> d.getCreatedAt() != null && d.getCreatedAt().toLocalDate().equals(today))
                .map(this::toDonationRow)
                .toList();
    }

    private List<AISearchRow> getAvailableDonations() {
        return donationRepository.findByStatus("AVAILABLE").stream()
                .map(this::toDonationRow)
                .toList();
    }

    private List<AISearchRow> getPickupsByStatus(String status) {
        return pickupRepository.findByStatus(status).stream()
                .map(this::toPickupRow)
                .toList();
    }

    private List<AISearchRow> getMyPickups(UUID userId) {
        if (userId == null) {
            return List.of();
        }

        return pickupRepository.findByDriverId(userId).stream()
                .map(this::toPickupRow)
                .toList();
    }

    private AISearchRow toDonationRow(Donation donation) {
        return AISearchRow.builder()
                .id(donation.getId())
                .foodName(donation.getFoodName())
                .status(donation.getStatus())
                .pickupTime(null)
                .deliveryTime(null)
                .createdDate(donation.getCreatedAt())
                .build();
    }

    private AISearchRow toPickupRow(PickupRequest pickupRequest) {
        Donation donation = donationRepository.findById(pickupRequest.getDonationId()).orElse(null);
        LocalDateTime createdDate = donation != null ? donation.getCreatedAt() : null;
        String foodName = donation != null ? donation.getFoodName() : "-";

        return AISearchRow.builder()
                .id(pickupRequest.getId())
                .foodName(foodName)
                .status(pickupRequest.getStatus())
                .pickupTime(pickupRequest.getPickupTime())
                .deliveryTime(pickupRequest.getDeliveryTime())
                .createdDate(createdDate)
                .build();
    }

    private String classifyCommand(String message) {
        String normalizedMessage = message == null ? "" : message.trim().toLowerCase();

        String prompt = "Classify this message into one command:\n"
                + "today_donations,\n"
                + "available_donations,\n"
                + "assigned_orders,\n"
                + "delivered_orders,\n"
                + "my_pickups.\n\n"
                + "Message: " + normalizedMessage;

        try {
            String modelText = callHuggingFace(prompt).toLowerCase();
            String commandFromModel = parseCommand(modelText);
            if (commandFromModel != null) {
                return commandFromModel;
            }
        } catch (Exception ignored) {
        }

        return fallbackCommand(normalizedMessage);
    }

    private String callHuggingFace(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!huggingFaceToken.isBlank()) {
            headers.setBearerAuth(huggingFaceToken);
        }

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(Map.of("inputs", prompt), headers);
        ResponseEntity<Object> response = restTemplate.exchange(
                huggingFaceApiUrl,
                HttpMethod.POST,
                requestEntity,
                Object.class);

        Object body = response.getBody();
        if (body instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?, ?> map) {
                Object text = map.get("generated_text");
                return text == null ? "" : text.toString();
            }
        }

        if (body instanceof Map<?, ?> map) {
            Object text = map.get("generated_text");
            return text == null ? "" : text.toString();
        }

        return "";
    }

    private String parseCommand(String modelText) {
        if (modelText.contains(TODAY_DONATIONS)) {
            return TODAY_DONATIONS;
        }
        if (modelText.contains(AVAILABLE_DONATIONS)) {
            return AVAILABLE_DONATIONS;
        }
        if (modelText.contains(ASSIGNED_ORDERS)) {
            return ASSIGNED_ORDERS;
        }
        if (modelText.contains(DELIVERED_ORDERS)) {
            return DELIVERED_ORDERS;
        }
        if (modelText.contains(MY_PICKUPS)) {
            return MY_PICKUPS;
        }
        return null;
    }

    private String fallbackCommand(String message) {
        if (message.contains("today") && message.contains("donation")) {
            return TODAY_DONATIONS;
        }
        if (message.contains("available") && message.contains("donation")) {
            return AVAILABLE_DONATIONS;
        }
        if (message.contains("assigned")) {
            return ASSIGNED_ORDERS;
        }
        if (message.contains("delivered")) {
            return DELIVERED_ORDERS;
        }
        if (message.contains("my pickup") || message.contains("my pickups") || message.contains("pickup")) {
            return MY_PICKUPS;
        }
        return AVAILABLE_DONATIONS;
    }
}
