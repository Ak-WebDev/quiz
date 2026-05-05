package com.quizapp.quizbackend.attempt;

import com.quizapp.quizbackend.email.EmailService;
import com.quizapp.quizbackend.quiz.Option;
import com.quizapp.quizbackend.quiz.Question;
import com.quizapp.quizbackend.quiz.Quiz;
import com.quizapp.quizbackend.quiz.QuizService;
import com.quizapp.quizbackend.security.CustomUserDetails;
import com.quizapp.quizbackend.user.User;
import com.quizapp.quizbackend.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    private final QuizService quizService;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public AttemptService(QuizService quizService,
                          QuizAttemptRepository quizAttemptRepository,
                          QuizAnswerRepository quizAnswerRepository,
                          UserRepository userRepository, EmailService emailService) {
        this.quizService = quizService;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAnswerRepository = quizAnswerRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public QuizSubmissionResponse submitQuiz(Long quizId,
                                             QuizSubmissionRequest request,
                                             Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Quiz quiz = quizService.getQuizById(quizId);

        // enforce single attempt per user per quiz
        quizAttemptRepository.findByQuizAndUser(quiz, user).ifPresent(existing -> {
            throw new IllegalStateException("Quiz already attempted by this user");
        });

        LocalDateTime now = LocalDateTime.now();

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .user(user)
                .startedAt(now) // in real app you'd track separate start time
                .submittedAt(now)
                .totalQuestions(quiz.getQuestions().size())
                .score(0) // set later
                .build();

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);

        // Create a quick lookup for questions & options
        Map<Long, Question> questionMap = new HashMap<>();
        Map<Long, Option> optionMap = new HashMap<>();

        quiz.getQuestions().forEach(q -> {
            questionMap.put(q.getId(), q);
            q.getOptions().forEach(o -> optionMap.put(o.getId(), o));
        });

        int score = 0;

        for (QuizSubmissionRequest.AnswerDto answerDto : request.getAnswers()) {
            Question question = questionMap.get(answerDto.getQuestionId());
            if (question == null) {
                throw new IllegalArgumentException("Invalid question id: " + answerDto.getQuestionId());
            }

            Option selectedOption = optionMap.get(answerDto.getSelectedOptionId());
            if (selectedOption == null || !selectedOption.getQuestion().getId().equals(question.getId())) {
                throw new IllegalArgumentException("Invalid option for question id: " + question.getId());
            }

            if (selectedOption.isCorrect()) {
                score++;
            }

            QuizAnswer answer = QuizAnswer.builder()
                    .attempt(savedAttempt)
                    .question(question)
                    .selectedOption(selectedOption)
                    .build();

            quizAnswerRepository.save(answer);
        }

        savedAttempt.setScore(score);
        quizAttemptRepository.save(savedAttempt);

        emailService.sendQuizResultEmail(user.getEmail(), user.getUsername(), quiz.getTitle(),
                score, savedAttempt.getTotalQuestions());

        return new QuizSubmissionResponse(savedAttempt.getId(), score, savedAttempt.getTotalQuestions());
    }

    public List<AttemptSummaryResponse> getAttemptsForCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<QuizAttempt> attempts = quizAttemptRepository.findByUser(user);

        return attempts.stream().map(
                a->new AttemptSummaryResponse(
                        a.getId(),
                        a.getQuiz().getId(),
                        a.getQuiz().getTitle(),
                        a.getScore(),
                        a.getTotalQuestions(),
                        a.getSubmittedAt()
                ))
                .collect(Collectors.toList());
    }
}