
package com.example.apilifecycle.controller;

import com.example.apilifecycle.service.ApiConsumerService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/consumers")
public class ApiConsumerController {

    private final ApiConsumerService consumerService;

    public ApiConsumerController(
            ApiConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registerConsumer(
            @RequestBody Map<String, String> request) {

        String name = request.get("name");

        Map<String, Object> response =
                consumerService.registerConsumer(name);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
