package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record AdminUpdateUserRequest(
		@Size(max = 120) @JsonProperty("name") String name,
		@Size(max = 32) @JsonProperty("phone") String phone,
		@Size(max = 255) @JsonProperty("location") String location,
		@JsonProperty("role") String role
) {
}
