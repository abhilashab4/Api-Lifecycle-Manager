
package com.example.apilifecycle.controller;

import com.example.apilifecycle.dto.CreateApiRequest;
import com.example.apilifecycle.entity.ApiVersion;
import com.example.apilifecycle.service.ApiLifecycleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registry")
@Validated
public class ApiRegistryController {

    private final ApiLifecycleService service;

    public ApiRegistryController(ApiLifecycleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiVersion register(
            @Valid @RequestBody CreateApiRequest request) {
        return service.register(request);
    }

    @GetMapping
    public List<ApiVersion> getAll() {
        return service.getAllApis();
    }

    @GetMapping("/{apiName}/{version}")
    public ApiVersion getApi(
            @PathVariable String apiName,
            @PathVariable String version) {
        return service.getApi(apiName, version);
    }

    @PutMapping("/{apiName}/{version}/deprecate")
    public ApiVersion deprecate(
            @PathVariable String apiName,
            @PathVariable String version,
            @Valid @RequestBody DeprecationRequest request) {

        return service.deprecate(
                apiName,
                version,
                request.sunsetDate(),
                request.replacementVersion());
    }

    @PutMapping("/{apiName}/{version}/retire")
    public ApiVersion retire(
            @PathVariable String apiName,
            @PathVariable String version) {
        return service.retire(apiName, version);
    }

    @GetMapping("/{apiName}/{version}/usage")
    public Map<String, Object> usageReport(
            @PathVariable String apiName,
            @PathVariable String version) {
        return service.getUsageReport(apiName, version);
    }

    public record DeprecationRequest(
            @Future(message = "Sunset date must be in the future")
            LocalDate sunsetDate,

            String replacementVersion) {
    }
}
