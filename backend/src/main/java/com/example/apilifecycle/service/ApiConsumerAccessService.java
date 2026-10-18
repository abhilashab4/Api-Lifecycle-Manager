
package com.example.apilifecycle.service;

import com.example.apilifecycle.entity.ApiConsumer;
import com.example.apilifecycle.entity.ApiConsumerAccess;
import com.example.apilifecycle.repository.ApiConsumerAccessRepository;
import com.example.apilifecycle.repository.ApiConsumerRepository;
import com.example.apilifecycle.repository.ApiVersionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class ApiConsumerAccessService {

    private final ApiConsumerRepository consumerRepository;
    private final ApiConsumerAccessRepository accessRepository;
    private final ApiVersionRepository apiVersionRepository;

    public ApiConsumerAccessService(
            ApiConsumerRepository consumerRepository,
            ApiConsumerAccessRepository accessRepository,
            ApiVersionRepository apiVersionRepository) {
        this.consumerRepository = consumerRepository;
        this.accessRepository = accessRepository;
        this.apiVersionRepository = apiVersionRepository;
    }

    @Transactional
    public Map<String, Object> grantAccess(
            Long consumerId,
            String apiName,
            String apiVersion) {

        ApiConsumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Consumer not found"));

        if (apiName == null || apiName.isBlank()
                || apiVersion == null || apiVersion.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "API name and version are required");
        }

        String name = apiName.trim();
        String version = apiVersion.trim();

        boolean apiExists = apiVersionRepository
                .findByApiNameAndVersion(name, version)
                .isPresent();

        if (!apiExists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "API version not found");
        }

        boolean alreadyGranted = accessRepository
                .existsByConsumerAndApiNameAndApiVersion(
                        consumer, name, version);

        if (alreadyGranted) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Access has already been granted");
        }

        ApiConsumerAccess access =
                new ApiConsumerAccess(consumer, name, version);

        accessRepository.save(access);

        return Map.of(
                "consumerId", consumer.getId(),
                "consumerName", consumer.getName(),
                "apiName", name,
                "apiVersion", version,
                "message", "API access granted successfully"
        );
    }

    public List<Map<String, Object>> getConsumerAccess(Long consumerId) {
        ApiConsumer consumer = consumerRepository.findById(consumerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Consumer not found"));

        return accessRepository.findByConsumer(consumer)
                .stream()
                .map(access -> Map.<String, Object>of(
                        "apiName", access.getApiName(),
                        "apiVersion", access.getApiVersion(),
                        "grantedAt", access.getGrantedAt()
                ))
                .toList();
    }



    public void checkAccess(
            ApiConsumer consumer,
            String apiName,
            String apiVersion) {

        boolean hasAccess = accessRepository
                .existsByConsumerAndApiNameAndApiVersion(
                        consumer, apiName, apiVersion);

        if (!hasAccess) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Consumer does not have access to this API version");
        }
    }

}
