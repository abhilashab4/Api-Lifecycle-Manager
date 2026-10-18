package com.example.apilifecycle.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_usage")
public class ApiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String apiName;

    @Column(nullable = false, length = 30)
    private String version;

    @Column(nullable = false, length = 100)
    private String consumer;

    @Column(nullable = false)
    private Instant requestedAt;

    protected ApiUsage() {
        // Required by JPA
    }

    public ApiUsage(
            String apiName,
            String version,
            String consumer) {
        this.apiName = apiName;
        this.version = version;
        this.consumer = consumer;
        this.requestedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getApiName() {
        return apiName;
    }

    public String getVersion() {
        return version;
    }

    public String getConsumer() {
        return consumer;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }
}