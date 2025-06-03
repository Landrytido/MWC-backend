package com.mywebcompanion.backendspring.controller;

import com.mywebcompanion.backendspring.dto.UserDto;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.security.ClerkService;
import com.mywebcompanion.backendspring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ClerkService clerkService;

    @PostMapping("/sync")
    public ResponseEntity<UserDto> syncUser(@RequestBody UserDto userDto) {
        User user = userService.syncUser(userDto);

        UserDto responseDto = new UserDto();
        responseDto.setClerkId(user.getClerkId());
        responseDto.setEmail(user.getEmail());
        responseDto.setFirstName(user.getFirstName());
        responseDto.setLastName(user.getLastName());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String clerkId = clerkService.extractClerkId(authentication);
            User user = userService.findByClerkIdMinimal(clerkId);

            UserDto responseDto = new UserDto();
            responseDto.setClerkId(user.getClerkId());
            responseDto.setEmail(user.getEmail());
            responseDto.setFirstName(user.getFirstName());
            responseDto.setLastName(user.getLastName());

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}