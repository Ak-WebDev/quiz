package com.quizapp.quizbackend.quiz;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private final QuizService quizService;

    public AdminQuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public QuizSummaryResponse createQuiz(@RequestBody QuizCreateRequest request) {
        return quizService.createQuiz(request);
    }

    @GetMapping
    public List<QuizSummaryResponse> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }

    // update/delete endpoints will be added later
}