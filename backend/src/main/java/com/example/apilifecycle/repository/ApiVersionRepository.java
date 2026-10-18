package com.example.apilifecycle.repository;

import com.example.apilifecycle.entity.ApiVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiVersionRepository
        extends JpaRepository<ApiVersion, Long> {

    Optional<ApiVersion> findByApiNameAndVersion(
            String apiName,
            String version);

    List<ApiVersion> findByApiName(String apiName);
}