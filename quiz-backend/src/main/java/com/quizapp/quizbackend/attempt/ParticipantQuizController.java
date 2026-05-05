package com.quizapp.quizbackend.attempt;

import com.quizapp.quizbackend.quiz.QuizDetailResponse;
import com.quizapp.quizbackend.quiz.QuizService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant/quizzes")
@PreAuthorize("hasRole('PARTICIPANT')")
public class ParticipantQuizController {

    private final QuizService quizService;
    private final AttemptService attemptService;

    public ParticipantQuizController(QuizService quizService, AttemptService attemptService) {
        this.quizService = quizService;
        this.attemptService = attemptService;
    }

    @GetMapping
    public List<?> listQuizzes() {
        return quizService.getAllQuizzes();
    }

    @GetMapping("/{id}")
    public QuizDetailResponse getQuiz(@PathVariable Long id) {
        return quizService.getQuizDetail(id);
    }

    @PostMapping("/{id}/submit")
    public QuizSubmissionResponse submitQuiz(@PathVariable Long id, @RequestBody QuizSubmissionRequest request,
                                             Authentication authentication) {
        return attemptService.submitQuiz(id, request, authentication);
    }
}