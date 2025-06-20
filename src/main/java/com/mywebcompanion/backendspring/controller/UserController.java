package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.UserDto;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();

            User user = userService.findByEmail(email);

            UserDto responseDto = new UserDto();
            responseDto.setEmail(user.getEmail());
            responseDto.setFirstName(user.getFirstName());
            responseDto.setLastName(user.getLastName());

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDto updateRequest) {

        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);

        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        User updatedUser = userService.updateUser(user);

        // Utiliser le builder pour un DTO complet
        UserDto responseDto = UserDto.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .enabled(updatedUser.getEnabled())
                .emailVerified(updatedUser.getEmailVerified())
                .createdAt(updatedUser.getCreatedAt())
                .updatedAt(updatedUser.getUpdatedAt())
                .build();

        return ResponseEntity.ok(responseDto);
    }
}