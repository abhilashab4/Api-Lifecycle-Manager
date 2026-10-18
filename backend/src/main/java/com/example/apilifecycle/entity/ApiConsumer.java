
package com.example.apilifecycle.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_consumers")
public class ApiConsumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String apiKeyHash;

    @Column(nullable = false)
    private Instant createdAt;

    protected ApiConsumer() {
        // Required by JPA
    }

    public ApiConsumer(String name, String apiKeyHash) {
        this.name = name;
        this.apiKeyHash = apiKeyHash;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKeyHash() {
        return apiKeyHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
