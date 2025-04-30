package com.mywebcompanion.backendspring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mywebcompanion.backendspring.model.ExampleEntity;

public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
}