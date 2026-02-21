package com.nikhil.ecommerce_backend.config;

import com.nikhil.ecommerce_backend.constants.RoleAuthority;
import com.nikhil.ecommerce_backend.entities.Role;
import com.nikhil.ecommerce_backend.entities.User;
import com.nikhil.ecommerce_backend.exceptions.ResourceNotFoundException;
import com.nikhil.ecommerce_backend.repositories.RoleRepository;
import com.nikhil.ecommerce_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.mail}")
    private String adminMail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        for (RoleAuthority authority : RoleAuthority.values()) {
            roleRepository.findByAuthority(authority).orElseGet(() -> {
                Role role = new Role();
                role.setAuthority(authority);
                return roleRepository.save(role);
            });
        }
        userRepository.findByEmailAndIsDeletedFalse(adminMail).orElseGet(() -> {
            User admin = new User();
            admin.setFirstName("System");
            admin.setLastName("Admin");
            admin.setEmail(adminMail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setActive(true);
            admin.setLocked(false);
            admin.setDeleted(false);
            admin.setRoles(new HashSet<>());

            Role adminRole = (Role) roleRepository.findByAuthority(RoleAuthority.ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin role not found"));
            admin.getRoles().add(adminRole);

            return userRepository.save(admin);
        });
    }
}
