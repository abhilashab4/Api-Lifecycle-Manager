
package com.example.apilifecycle.repository;

import com.example.apilifecycle.entity.ApiUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiUsageRepository
        extends JpaRepository<ApiUsage, Long> {

    List<ApiUsage> findByApiNameAndVersion(
            String apiName,
            String version);
}
