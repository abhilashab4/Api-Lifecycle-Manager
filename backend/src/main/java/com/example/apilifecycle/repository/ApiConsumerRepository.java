
package com.example.apilifecycle.repository;

import com.example.apilifecycle.entity.ApiConsumer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiConsumerRepository
        extends JpaRepository<ApiConsumer, Long> {

    Optional<ApiConsumer> findByName(String name);

    Optional<ApiConsumer> findByApiKeyHash(String apiKeyHash);

    boolean existsByName(String name);
}
