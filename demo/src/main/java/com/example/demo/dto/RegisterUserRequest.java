package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
		@NotBlank @Size(max = 120) @JsonProperty("name") String name,
		@NotBlank @Size(max = 32) @JsonProperty("phone") String phone,
		@Size(max = 255) @JsonProperty("location") String location,
		@NotBlank @Size(min = 4, max = 128) @JsonProperty("password") String password
) {
}
