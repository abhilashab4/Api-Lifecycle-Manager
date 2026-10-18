
package com.example.apilifecycle.service;

import com.example.apilifecycle.entity.ApiConsumer;
import com.example.apilifecycle.repository.ApiConsumerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;

@Service
public class ApiConsumerService {

    private final ApiConsumerRepository consumerRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiConsumerService(
            ApiConsumerRepository consumerRepository) {
        this.consumerRepository = consumerRepository;
    }

    @Transactional
    public Map<String, Object> registerConsumer(String name) {

        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Consumer name is required");
        }

        String consumerName = name.trim();

        if (consumerName.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Consumer name must not exceed 100 characters");
        }

        if (consumerRepository.existsByName(consumerName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Consumer already exists");
        }

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String apiKey = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String apiKeyHash = hashApiKey(apiKey);

        ApiConsumer consumer = new ApiConsumer(
                consumerName,
                apiKeyHash);

        consumerRepository.save(consumer);

        // Return the raw key only at registration time.
        return Map.of(
                "id", consumer.getId(),
                "name", consumer.getName(),
                "apiKey", apiKey,
                "message", "Save this API key securely. It will not be shown again."
        );
    }

    private String hashApiKey(String apiKey) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    apiKey.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is not available", e);
        }
    }



    public ApiConsumer authenticateConsumer(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "API key is required");
        }

        String apiKeyHash = hashApiKey(apiKey);

        return consumerRepository.findByApiKeyHash(apiKeyHash)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                org.springframework.http.HttpStatus.UNAUTHORIZED,
                                "Invalid API key"));
    }

}
