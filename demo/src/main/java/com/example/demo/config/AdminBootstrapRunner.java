package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${app.admin.bootstrap.phone}")
	private String adminPhone;

	@Value("${app.admin.bootstrap.name}")
	private String adminName;

	@Value("${app.admin.bootstrap.password}")
	private String adminPassword;

	public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		String phone = adminPhone.trim();
		userRepository.findByPhone(phone).ifPresentOrElse(
				u -> {
					boolean dirty = false;
					if (!User.ROLE_ADMIN.equalsIgnoreCase(safeRole(u.getRole()))) {
						u.setRole(User.ROLE_ADMIN);
						dirty = true;
					}
					if (u.getPasswordHash() == null || u.getPasswordHash().isBlank()) {
						u.setPasswordHash(passwordEncoder.encode(adminPassword));
						dirty = true;
					}
					if (dirty) {
						userRepository.save(u);
					}
				},
				() -> {
					User admin = new User();
					admin.setName(adminName);
					admin.setPhone(phone);
					admin.setLocation("System");
					admin.setPasswordHash(passwordEncoder.encode(adminPassword));
					admin.setRole(User.ROLE_ADMIN);
					userRepository.save(admin);
				});
	}

	private static String safeRole(String role) {
		return role == null ? "" : role.trim();
	}
}
