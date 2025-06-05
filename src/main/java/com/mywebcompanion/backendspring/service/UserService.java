package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.UserDto;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User syncUser(UserDto userDto) {
        User user = userRepository.findByClerkId(userDto.getClerkId())
                .orElse(new User());

        user.setClerkId(userDto.getClerkId());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return userRepository.save(user);
    }

    // ✅ MÉTHODE SIMPLE - évite le chargement des collections
    public User findByClerkIdSimple(String clerkId) {
        return userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ MÉTHODE AVEC BLOC NOTE si nécessaire
    public User findByClerkIdWithBlocNote(String clerkId) {
        return userRepository.findByClerkIdWithBlocNote(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ Alias pour compatibilité - utilisera la méthode simple
    public User findByClerkId(String clerkId) {
        return findByClerkIdSimple(clerkId);
    }
}