package com.quizapp.quizbackend.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizSubmissionResponse {
    private Long attemptId;
    private int score;
    private int totalQuestions;
}