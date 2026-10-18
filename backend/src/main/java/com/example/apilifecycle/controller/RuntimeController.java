
package com.example.apilifecycle.controller;

import com.example.apilifecycle.entity.ApiConsumer;
import com.example.apilifecycle.entity.ApiStatus;
import com.example.apilifecycle.entity.ApiVersion;
import com.example.apilifecycle.service.ApiConsumerAccessService;
import com.example.apilifecycle.service.ApiConsumerService;
import com.example.apilifecycle.service.ApiLifecycleService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/runtime")
public class RuntimeController {

    private final ApiLifecycleService service;
    private final ApiConsumerService consumerService;
    private final ApiConsumerAccessService accessService;

    public RuntimeController(
            ApiLifecycleService service,
            ApiConsumerService consumerService,
            ApiConsumerAccessService accessService) {

        this.service = service;
        this.consumerService = consumerService;
        this.accessService = accessService;
    }

    @GetMapping("/{apiName}/{version}")
    public ResponseEntity<Map<String, Object>> callApi(
            @PathVariable String apiName,
            @PathVariable String version,
            @RequestHeader(
                    value = "X-API-Key",
                    required = false) String apiKey) {

        // 1. Authenticate the registered consumer
        ApiConsumer consumer =
                consumerService.authenticateConsumer(apiKey);

        // 2. Find the requested API version
        ApiVersion api = service.getApi(apiName, version);

        // 3. Reject retired versions
        if (api.getStatus() == ApiStatus.RETIRED) {
            throw new ResponseStatusException(
                    HttpStatus.GONE,
                    "This API version has been retired");
        }

        // 4. Verify that this consumer has permission
        accessService.checkAccess(consumer, apiName, version);

        // 5. Record successful usage
        service.recordUsage(apiName, version, consumer.getName());

        // 6. Prepare lifecycle response headers
        HttpHeaders headers = new HttpHeaders();

        if (api.getStatus() == ApiStatus.DEPRECATED) {

            if (api.getSunsetDate() != null) {

                long epochSeconds = api.getSunsetDate()
                        .atStartOfDay(ZoneOffset.UTC)
                        .toEpochSecond();

                headers.set("Deprecation", "@" + epochSeconds);

                String sunset = api.getSunsetDate()
                        .atStartOfDay(ZoneOffset.UTC)
                        .format(DateTimeFormatter.RFC_1123_DATE_TIME);

                headers.set("Sunset", sunset);
            }

            if (api.getReplacementVersion() != null) {
                headers.set(
                        "Link",
                        "</runtime/" + apiName + "/"
                                + api.getReplacementVersion()
                                + ">; rel=\"successor-version\"");
            }
        }

        // 7. Return the simulated API response
        return ResponseEntity.ok()
                .headers(headers)
                .body(Map.of(
                        "message", "API request successful",
                        "apiName", apiName,
                        "version", version,
                        "consumer", consumer.getName(),
                        "status", api.getStatus()
                ));
    }
}
