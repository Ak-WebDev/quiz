package com.quizapp.quizbackend.attempt;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuizSubmissionRequest {

    private List<AnswerDto> answers;

    @Getter
    @Setter
    public static class AnswerDto {
        private Long questionId;
        private Long selectedOptionId;
    }
}