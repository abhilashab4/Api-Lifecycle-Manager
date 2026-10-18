package com.example.apilifecycle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateApiRequest(

        @NotBlank(message = "API name is required")
        @Size(max = 100, message = "API name cannot exceed 100 characters")
        String apiName,

        @NotBlank(message = "API version is required")
        @Size(max = 30, message = "API version cannot exceed 30 characters")
        String version,

        @NotBlank(message = "Owner is required")
        @Size(max = 100, message = "Owner cannot exceed 100 characters")
        String owner

) {
}