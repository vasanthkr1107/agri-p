package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserPublicDto(
		@JsonProperty("id") Long id,
		@JsonProperty("name") String name,
		@JsonProperty("phone") String phone,
		@JsonProperty("location") String location,
		@JsonProperty("role") String role
) {
}
