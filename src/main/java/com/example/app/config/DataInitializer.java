package com.example.app.config;

import com.example.app.modules.role.Role;
import com.example.app.modules.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Initialize default roles if they don't exist
        initializeRole("ROLE_USER");
        initializeRole("ROLE_ADMIN");
    }

    private void initializeRole(String roleName) {
        roleRepository.findByName(roleName)
                .ifPresentOrElse(
                        role -> log.debug("Role '{}' already exists", roleName),
                        () -> {
                            Role newRole = Role.builder()
                                    .name(roleName)
                                    .build();
                            roleRepository.save(newRole);
                            log.info("Created role: {}", roleName);
                        });
    }
}
