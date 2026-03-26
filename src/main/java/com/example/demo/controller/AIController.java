package com.example.demo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.AISearchRequest;
import com.example.demo.model.AISearchResponse;
import com.example.demo.service.AIService;

@RestController
@RequestMapping("/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/search")
    public AISearchResponse search(@RequestBody AISearchRequest request) {
        return aiService.search(request);
    }
}
