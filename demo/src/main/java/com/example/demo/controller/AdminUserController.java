package com.example.demo.controller;

import com.example.demo.dto.AdminUpdateUserRequest;
import com.example.demo.dto.UserPublicDto;
import com.example.demo.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping
	public List<UserPublicDto> list() {
		return adminUserService.listAll();
	}

	@GetMapping("/{id}")
	public UserPublicDto get(@PathVariable Long id) {
		return adminUserService.getById(id);
	}

	@PutMapping("/{id}")
	public UserPublicDto update(@PathVariable Long id, @Valid @RequestBody AdminUpdateUserRequest body) {
		return adminUserService.update(id, body);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
		adminUserService.delete(id);
		Map<String, String> response = new HashMap<>();
		response.put("success", "true");
		response.put("message", "User removed.");
		return ResponseEntity.ok(response);
	}
}
