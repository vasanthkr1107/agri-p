package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterUserRequest;
import com.example.demo.dto.UserPublicDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.AuthTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthTokenService authTokenService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthTokenService authTokenService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authTokenService = authTokenService;
	}

	public LoginResponse login(LoginRequest request) {
		String id = request.identifier().trim();
		User user = userRepository.findByPhone(id)
				.or(() -> userRepository.findByNameIgnoreCase(id))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
		if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Account has no password set; contact admin");
		}
		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		String role = normalizeRole(user.getRole());
		String token = authTokenService.createToken(user.getId(), role);
		return new LoginResponse(token, toDto(user, role));
	}

	public User register(RegisterUserRequest request) {
		if (userRepository.existsByPhone(request.phone().trim())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already registered");
		}
		User user = new User();
		user.setName(request.name().trim());
		user.setPhone(request.phone().trim());
		user.setLocation(request.location() != null ? request.location().trim() : null);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		user.setRole(User.ROLE_USER);
		return userRepository.save(user);
	}

	private static UserPublicDto toDto(User user, String role) {
		return new UserPublicDto(
				user.getId(),
				user.getName(),
				user.getPhone(),
				user.getLocation(),
				role);
	}

	private static String normalizeRole(String role) {
		if (role == null || role.isBlank()) {
			return User.ROLE_USER;
		}
		return User.ROLE_ADMIN.equalsIgnoreCase(role) ? User.ROLE_ADMIN : User.ROLE_USER;
	}
}
