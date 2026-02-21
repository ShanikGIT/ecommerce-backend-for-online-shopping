package com.nikhil.ecommerce_backend.security;

import com.nikhil.ecommerce_backend.entities.User;
import com.nikhil.ecommerce_backend.exceptions.ResourceNotFoundException;
import com.nikhil.ecommerce_backend.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> role.getAuthority().toString())
                        .toArray(String[]::new))
                .accountLocked(user.isLocked())
                .disabled(!user.isActive())
                .build();
    }
}
