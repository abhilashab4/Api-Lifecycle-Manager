
package com.example.apilifecycle.controller;

import com.example.apilifecycle.service.ApiConsumerAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consumer-access")
public class ApiConsumerAccessController {

    private final ApiConsumerAccessService accessService;

    public ApiConsumerAccessController(
            ApiConsumerAccessService accessService) {
        this.accessService = accessService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> grantAccess(
            @RequestBody Map<String, Object> request) {

        Long consumerId = Long.valueOf(
                request.get("consumerId").toString());

        String apiName = (String) request.get("apiName");
        String apiVersion = (String) request.get("apiVersion");

        Map<String, Object> response = accessService.grantAccess(
                consumerId, apiName, apiVersion);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{consumerId}")
    public ResponseEntity<List<Map<String, Object>>> getConsumerAccess(
            @PathVariable Long consumerId) {

        return ResponseEntity.ok(
                accessService.getConsumerAccess(consumerId));
    }
}
