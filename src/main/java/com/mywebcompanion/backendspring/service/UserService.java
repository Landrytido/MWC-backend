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

    public User findByClerkId(String clerkId) {
        System.out.println("🚨🚨🚨 UserService.findByClerkId - clerkId: " + clerkId);
        Thread.dumpStack();
        return userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}