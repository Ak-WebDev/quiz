package com.quizapp.quizbackend.quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class QuizDetailResponse {
    private Long id;
    private String title;
    private String description;
    private int timeLimitMinutes;
    private List<QuestionDetail> questions;

    @Getter
    @AllArgsConstructor
    public static class QuestionDetail {
        private Long id;
        private String text;
        private List<OptionDetail> options;
    }

    @Getter
    @AllArgsConstructor
    public static class OptionDetail {
        private Long id;
        private String text;
    }
}