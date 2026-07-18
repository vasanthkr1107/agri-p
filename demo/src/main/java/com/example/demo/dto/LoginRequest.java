package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@NotBlank(message = "identifier is required")
		@JsonProperty("identifier")
		String identifier,
		@NotBlank(message = "password is required")
		@JsonProperty("password")
		String password
) {
}
