package com.mywebcompanion.backendspring.repository;

import com.mywebcompanion.backendspring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ REQUÊTE SIMPLE - évite le chargement des collections
    @Query("SELECT u FROM User u WHERE u.clerkId = :clerkId")
    Optional<User> findByClerkId(@Param("clerkId") String clerkId);

    // ✅ REQUÊTE SPÉCIFIQUE pour BlocNote seulement
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.blocNote WHERE u.clerkId = :clerkId")
    Optional<User> findByClerkIdWithBlocNote(@Param("clerkId") String clerkId);

    Optional<User> findByEmail(String email);
}