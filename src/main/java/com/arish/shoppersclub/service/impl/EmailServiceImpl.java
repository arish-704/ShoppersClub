package com.arish.shoppersclub.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for dispatching email notifications via Spring JavaMailSender.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    /**
     * Sends a simple text email to notify the user of a successful login.
     * Wrapped in a try-catch block to prevent email errors from failing the login HTTP response.
     *
     * @param toEmail User's email address
     */
    @Override
    public void sendLoginNotification(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("furyoflegends7777@gmail.com");
            message.setTo(toEmail);
            message.setSubject("Successful Login - Welcome back to ShoppersClub!");
            message.setText("Hello,\n\nYou have successfully logged in to your ShoppersClub account.\n\nThank you for being a valued member of ShoppersClub!\n\nBest regards,\nShoppersClub Team");

            mailSender.send(message);
            log.info("Login notification email dispatched successfully to: {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send login notification email to {}: {}", toEmail, ex.getMessage());
        }
    }
}
