package com.example.apilifecycle.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "api_versions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_api_name_version",
                columnNames = {"api_name", "version"}
        )
)
public class ApiVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 100)
    private String apiName;

    @Column(nullable = false, length = 30)
    private String version;

    @Column(nullable = false, length = 100)
    private String owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApiStatus status = ApiStatus.ACTIVE;

    private LocalDate sunsetDate;

    @Column(length = 30)
    private String replacementVersion;

    protected ApiVersion() {
        // Required by JPA
    }

    public ApiVersion(
            String apiName,
            String version,
            String owner) {
        this.apiName = apiName;
        this.version = version;
        this.owner = owner;
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

    public String getOwner() {
        return owner;
    }

    public ApiStatus getStatus() {
        return status;
    }

    public LocalDate getSunsetDate() {
        return sunsetDate;
    }

    public String getReplacementVersion() {
        return replacementVersion;
    }

    public void deprecate(
            LocalDate sunsetDate,
            String replacementVersion) {

        if (status != ApiStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Only active API versions can be deprecated."
            );
        }

        this.status = ApiStatus.DEPRECATED;
        this.sunsetDate = sunsetDate;
        this.replacementVersion = replacementVersion;
    }

    public void retire() {
        if (status != ApiStatus.DEPRECATED) {
            throw new IllegalStateException(
                    "Only deprecated API versions can be retired."
            );
        }

        this.status = ApiStatus.RETIRED;
    }
}