
package com.example.apilifecycle.repository;

import com.example.apilifecycle.entity.ApiConsumer;
import com.example.apilifecycle.entity.ApiConsumerAccess;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiConsumerAccessRepository
        extends JpaRepository<ApiConsumerAccess, Long> {

    boolean existsByConsumerAndApiNameAndApiVersion(
            ApiConsumer consumer,
            String apiName,
            String apiVersion
    );

    List<ApiConsumerAccess> findByConsumer(ApiConsumer consumer);
}
