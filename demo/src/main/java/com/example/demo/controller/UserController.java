package com.example.demo.controller;

import com.example.demo.dto.RegisterUserRequest;
import com.example.demo.dto.UserPublicDto;
import com.example.demo.entity.User;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public self-registration only. Listing, details, update, and delete are under /api/admin/users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final AuthService authService;

	public UserController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping
	public UserPublicDto register(@Valid @RequestBody RegisterUserRequest request) {
		User user = authService.register(request);
		String role = user.getRole() == null || user.getRole().isBlank() ? User.ROLE_USER : user.getRole();
		return new UserPublicDto(user.getId(), user.getName(), user.getPhone(), user.getLocation(), role);
	}
}
