package com.quizapp.quizbackend.attempt;

import com.quizapp.quizbackend.user.User;
import com.quizapp.quizbackend.quiz.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    Optional<QuizAttempt> findByQuizAndUser(Quiz quiz, User user);

    List<QuizAttempt> findByUser(User user);
}