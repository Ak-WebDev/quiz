package com.quizapp.quizbackend.attempt;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant/attempts")
@PreAuthorize("hasRole('PARTICIPANT')")
public class ParticipantAttemptController {

    private final AttemptService attemptService;

    public ParticipantAttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @GetMapping
    public List<AttemptSummaryResponse> getMyAttempts(Authentication authentication) {
        return attemptService.getAttemptsForCurrentUser(authentication);
    }
}