package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
		@JsonProperty("token") String token,
		@JsonProperty("user") UserPublicDto user
) {
}
