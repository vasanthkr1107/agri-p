package com.example.demo.service;

import com.example.demo.dto.AdminUpdateUserRequest;
import com.example.demo.dto.UserPublicDto;
import com.example.demo.entity.User;
import com.example.demo.repository.PredictionRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminUserService {

	private final UserRepository userRepository;
	private final PredictionRepository predictionRepository;

	public AdminUserService(UserRepository userRepository, PredictionRepository predictionRepository) {
		this.userRepository = userRepository;
		this.predictionRepository = predictionRepository;
	}

	public List<UserPublicDto> listAll() {
		return userRepository.findAll().stream().map(this::toDto).toList();
	}

	public UserPublicDto getById(Long id) {
		return toDto(getEntity(id));
	}

	public UserPublicDto update(Long id, AdminUpdateUserRequest req) {
		User user = getEntity(id);
		if (req.name() != null && !req.name().isBlank()) {
			user.setName(req.name().trim());
		}
		if (req.phone() != null && !req.phone().isBlank()) {
			String p = req.phone().trim();
			userRepository.findByPhone(p).filter(u -> !u.getId().equals(id)).ifPresent(u -> {
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Phone already in use");
			});
			user.setPhone(p);
		}
		if (req.location() != null) {
			user.setLocation(req.location().isBlank() ? null : req.location().trim());
		}
		if (req.role() != null && !req.role().isBlank()) {
			String r = req.role().trim().toUpperCase();
			if (!User.ROLE_USER.equals(r) && !User.ROLE_ADMIN.equals(r)) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role must be USER or ADMIN");
			}
			user.setRole(r);
		}
		return toDto(userRepository.save(user));
	}

	@Transactional
	public void delete(Long id) {
		if (!userRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		// Admin UX: allow removing users even if they have predictions.
		// Predictions are deleted first to avoid FK/constraint issues and keep DB consistent.
		predictionRepository.deleteByUser_Id(id);
		userRepository.deleteById(id);
	}

	private User getEntity(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
	}

	private UserPublicDto toDto(User user) {
		String role = user.getRole() == null || user.getRole().isBlank() ? User.ROLE_USER : user.getRole();
		return new UserPublicDto(user.getId(), user.getName(), user.getPhone(), user.getLocation(), role);
	}
}
