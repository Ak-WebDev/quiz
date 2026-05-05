package com.quizapp.quizbackend.auth;

import com.quizapp.quizbackend.email.EmailService;
import com.quizapp.quizbackend.security.CustomUserDetails;
import com.quizapp.quizbackend.security.JwtService;
import com.quizapp.quizbackend.user.User;
import com.quizapp.quizbackend.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // we will tighten later to frontend domain
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final EmailService emailService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService, UserService userService,  EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        // default role PARTICIPANT for new signups
        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                Set.of("ROLE_PARTICIPANT")
        );
        emailService.sendRegistrationEmail(user.getEmail(), user.getUsername());

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(
                userDetails,
                Map.of("roles", userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority()).collect(Collectors.toSet()))
        );

        return new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet())
        );
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(
                userDetails,
                Map.of("roles", userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority()).collect(Collectors.toSet()))
        );

        return new AuthResponse(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toSet())
        );
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String token = ""; // frontend will already have token; this is more for convenience
        return new AuthResponse(
                token,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream()
                        .map(a -> a.getAuthority())
                        .collect(Collectors.toSet())
        );
    }
}