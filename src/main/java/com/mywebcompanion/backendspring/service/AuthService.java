package com.mywebcompanion.backendspring.service;

import com.mywebcompanion.backendspring.dto.*;
import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.UserRepository;
import com.mywebcompanion.backendspring.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        UserDto userDto = UserDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .enabled(savedUser.getEnabled())
                .emailVerified(savedUser.getEmailVerified())
                .createdAt(savedUser.getCreatedAt())
                .updatedAt(savedUser.getUpdatedAt())
                .build();

        return new AuthResponse(token, refreshToken, userDto);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getEnabled()) {
            throw new RuntimeException("Compte désactivé");
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.getEnabled())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return new AuthResponse(token, refreshToken, userDto);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            String email = jwtService.extractUsername(request.getRefreshToken());
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!user.getEnabled()) {
                throw new RuntimeException("Compte désactivé");
            }

            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            UserDto userDto = UserDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .enabled(user.getEnabled())
                    .emailVerified(user.getEmailVerified())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();

            return new AuthResponse(token, refreshToken, userDto);
        } catch (Exception e) {
            throw new RuntimeException("Refresh token invalide");
        }
    }

    public void logout(String token) {
        // TODO: Implémenter une blacklist des tokens si nécessaire

        // Pour l'instant, on fait juste expirer côté client
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Token de vérification invalide"));

        if (user.getEmailVerificationExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de vérification expiré");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationExpiry(null);
        userRepository.save(user);
    }

    // Méthode utilitaire pour générer un token de vérification d'email
    public void generateEmailVerificationToken(User user) {
        user.setEmailVerificationToken(UUID.randomUUID().toString());
        user.setEmailVerificationExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
    }
}