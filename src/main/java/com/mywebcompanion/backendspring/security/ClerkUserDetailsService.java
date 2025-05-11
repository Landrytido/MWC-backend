package com.mywebcompanion.backendspring.security;

import com.mywebcompanion.backendspring.model.User;
import com.mywebcompanion.backendspring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClerkUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String clerkId) throws UsernameNotFoundException {
        User user = userRepository.findByClerkId(clerkId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with clerkId: " + clerkId));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getClerkId())
                .password("") // Pas besoin de mot de passe avec Clerk
                .authorities("USER")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}