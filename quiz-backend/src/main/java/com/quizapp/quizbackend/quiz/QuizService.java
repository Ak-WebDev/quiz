package com.quizapp.quizbackend.quiz;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Transactional
    public QuizSummaryResponse createQuiz(QuizCreateRequest request) {
        Quiz quiz = Quiz.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .timeLimitMinutes(request.getTimeLimitMinutes())
                .build();

        var questionEntities = new ArrayList<Question>();

        request.getQuestions().forEach(questionDto -> {
            Question question = Question.builder()
                    .text(questionDto.getText())
                    .quiz(quiz)
                    .build();

            var optionEntities = new ArrayList<Option>();

            questionDto.getOptions().forEach(optionDto -> {
                Option option = Option.builder()
                        .text(optionDto.getText())
                        .correct(optionDto.isCorrect())
                        .question(question)
                        .build();
                optionEntities.add(option);
            });

            question.setOptions(optionEntities);
            questionEntities.add(question);
        });

        quiz.setQuestions(questionEntities);

        Quiz saved = quizRepository.save(quiz); // cascades to questions and options

        return new QuizSummaryResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getTimeLimitMinutes()
        );
    }

    public List<QuizSummaryResponse> getAllQuizzes() {
        return quizRepository.findAll().stream()
                .map(q -> new QuizSummaryResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getDescription(),
                        q.getTimeLimitMinutes()
                ))
                .toList();
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));
    }

    public QuizDetailResponse getQuizDetail(Long id) {
        Quiz quiz = getQuizById(id);

        var questionDetails = quiz.getQuestions().stream()
                .map(q -> new QuizDetailResponse.QuestionDetail(
                        q.getId(),
                        q.getText(),
                        q.getOptions().stream()
                                .map(o -> new QuizDetailResponse.OptionDetail(
                                        o.getId(),
                                        o.getText()
                                ))
                                .toList()
                ))
                .toList();

        return new QuizDetailResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getTimeLimitMinutes(),
                questionDetails
        );
    }
}