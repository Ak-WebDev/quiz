package com.quizapp.quizbackend.quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizCreateRequest {
    private String title;
    private String description;
    private int timeLimitMinutes;
    private List<QuestionDto> questions;

    @Getter
    @Setter
    public static class QuestionDto {
        private String text;
        private List<OptionDto> options;
    }

    @Getter
    @Setter
    public static class OptionDto {
        private String text;
        private boolean correct;
    }
}