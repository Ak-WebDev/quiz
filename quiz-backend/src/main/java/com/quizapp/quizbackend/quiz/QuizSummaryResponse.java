package com.quizapp.quizbackend.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuizSummaryResponse {
    private Long id;
    private String title;
    private String description;
    private int timeLimitMinutes;
}