package com.quizapp.quizbackend.attempt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttemptSummaryResponse {

    private Long attemptId;
    private Long quizId;
    private String quizTitle;
    private int score;
    private int totalQuestions;
    private LocalDateTime submittedAt;
}