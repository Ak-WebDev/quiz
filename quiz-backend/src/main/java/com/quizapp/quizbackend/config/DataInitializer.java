package com.quizapp.quizbackend.config;

import com.quizapp.quizbackend.user.Role;
import com.quizapp.quizbackend.user.RoleRepository;
import com.quizapp.quizbackend.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserService userService;

    public DataInitializer(RoleRepository roleRepository,
                           UserService userService) {
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Ensure roles exist
        createRoleIfNotExists("ROLE_ADMIN");
        createRoleIfNotExists("ROLE_PARTICIPANT");

        // Optional: create a default admin user if not present
        if (userService.findByUsername("admin").isEmpty()) {
            userService.createUser(
                    "admin",
                    "admin@example.com",
                    "admin123", // you can change this later
                    Set.of("ROLE_ADMIN")
            );
        }
    }

    private void createRoleIfNotExists(String roleName) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(
                        Role.builder().name(roleName).build()
                ));
    }
}