package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Advisory;

public interface AdvisoryRepository extends JpaRepository<Advisory, Long> {
}