
package com.example.apilifecycle.service;

import com.example.apilifecycle.dto.CreateApiRequest;
import com.example.apilifecycle.entity.ApiStatus;
import com.example.apilifecycle.entity.ApiUsage;
import com.example.apilifecycle.entity.ApiVersion;
import com.example.apilifecycle.repository.ApiUsageRepository;
import com.example.apilifecycle.repository.ApiVersionRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ApiLifecycleService {

    private final ApiVersionRepository versionRepository;
    private final ApiUsageRepository usageRepository;

    public ApiLifecycleService(
            ApiVersionRepository versionRepository,
            ApiUsageRepository usageRepository) {
        this.versionRepository = versionRepository;
        this.usageRepository = usageRepository;
    }

    @Transactional
    public ApiVersion register(CreateApiRequest request) {

        boolean exists = versionRepository
                .findByApiNameAndVersion(
                        request.apiName(),
                        request.version())
                .isPresent();

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This API version is already registered");
        }

        ApiVersion api = new ApiVersion(
                request.apiName(),
                request.version(),
                request.owner());

        return versionRepository.save(api);
    }

    public List<ApiVersion> getAllApis() {
        return versionRepository.findAll();
    }

    public ApiVersion getApi(
            String apiName,
            String version) {
        return findApi(apiName, version);
    }

    @Transactional
    public ApiVersion deprecate(
            String apiName,
            String version,
            LocalDate sunsetDate,
            String replacementVersion) {

        ApiVersion api = findApi(apiName, version);

        if (api.getStatus() != ApiStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only active APIs can be deprecated");
        }

        if (sunsetDate == null ||
                !sunsetDate.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Sunset date must be in the future");
        }

        if (replacementVersion != null &&
                replacementVersion.isBlank()) {
            replacementVersion = null;
        }

        if (version.equals(replacementVersion)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Replacement must be a different version");
        }

        if (replacementVersion != null) {
            ApiVersion replacement = findApi(
                    apiName, replacementVersion);

            if (replacement.getStatus() == ApiStatus.RETIRED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Replacement version is already retired");
            }
        }

        api.deprecate(sunsetDate, replacementVersion);

        return versionRepository.save(api);
    }

    @Transactional
    public ApiVersion retire(
            String apiName,
            String version) {

        ApiVersion api = findApi(apiName, version);

        if (api.getStatus() != ApiStatus.DEPRECATED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only deprecated APIs can be retired");
        }

        if (api.getSunsetDate() == null ||
                LocalDate.now().isBefore(api.getSunsetDate())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The sunset date has not been reached");
        }

        api.retire();

        return versionRepository.save(api);
    }

    @Transactional
    public void recordUsage(
            String apiName,
            String version,
            String consumer) {

        ApiVersion api = findApi(apiName, version);

        if (api.getStatus() == ApiStatus.RETIRED) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "This API version has been retired");
        }

        usageRepository.save(
                new ApiUsage(apiName, version, consumer));
    }

    public Map<String, Object> getUsageReport(
            String apiName,
            String version) {

        ApiVersion api = findApi(apiName, version);

        List<ApiUsage> usage =
                usageRepository.findByApiNameAndVersion(
                        apiName, version);

        List<String> consumers = usage.stream()
                .map(ApiUsage::getConsumer)
                .distinct()
                .sorted()
                .toList();

        return Map.of(
                "apiName", api.getApiName(),
                "version", api.getVersion(),
                "owner", api.getOwner(),
                "status", api.getStatus(),
                "totalRequests", usage.size(),
                "activeConsumers", consumers,
                "sunsetDate", api.getSunsetDate() == null
                        ? "" : api.getSunsetDate().toString(),
                "replacementVersion",
                api.getReplacementVersion() == null
                        ? "" : api.getReplacementVersion()
        );
    }

    private ApiVersion findApi(
            String apiName,
            String version) {

        return versionRepository
                .findByApiNameAndVersion(apiName, version)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "API version not found"));
    }
}
