
package com.example.apilifecycle.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "api_consumer_access",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"consumer_id", "api_name", "api_version"}
                )
        }
)
public class ApiConsumerAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "consumer_id", nullable = false)
    private ApiConsumer consumer;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(name = "api_version", nullable = false, length = 30)
    private String apiVersion;

    @Column(nullable = false)
    private Instant grantedAt;

    protected ApiConsumerAccess() {
        // Required by JPA
    }

    public ApiConsumerAccess(
            ApiConsumer consumer,
            String apiName,
            String apiVersion) {
        this.consumer = consumer;
        this.apiName = apiName;
        this.apiVersion = apiVersion;
        this.grantedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public ApiConsumer getConsumer() {
        return consumer;
    }

    public String getApiName() {
        return apiName;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }
}
