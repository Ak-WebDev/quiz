package com.quizapp.quizbackend.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    // set a default from; for Mailtrap any email is fine
    private final String from = "no-reply@quizapp.com";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String to, String username) {
        String subject = "Welcome to QuizApp";
        String text = "Hi " + username + ",\n\n"
                + "Your account has been created successfully.\n\n"
                + "Happy quizzing!\nQuizApp Team";

        sendSimpleMail(to, subject, text);
    }

    public void sendQuizResultEmail(String to, String username,
                                    String quizTitle, int score, int totalQuestions) {
        String subject = "Quiz Result: " + quizTitle;
        String text = "Hi " + username + ",\n\n"
                + "You have completed the quiz \"" + quizTitle + "\".\n"
                + "Score: " + score + " / " + totalQuestions + "\n\n"
                + "Thanks for participating!\nQuizApp Team";

        sendSimpleMail(to, subject, text);
    }

    private void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}