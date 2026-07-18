package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByPhone(String phone);

	Optional<User> findByPhone(String phone);

	Optional<User> findByNameIgnoreCase(String name);

	long countByRole(String role);
}
