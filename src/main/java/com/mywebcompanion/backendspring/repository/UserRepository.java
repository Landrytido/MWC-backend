package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClerkId(String clerkId);

    Optional<User> findByEmail(String email);
}