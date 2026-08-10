package com.arish.shoppersclub.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.arish.shoppersclub.service.EmailService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service implementation for dispatching simple email notifications.
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    private static final String SENDER_EMAIL = "furyoflegends7777@gmail.com";

    @Override
    public void sendLoginNotification(String toEmail) {
        if (mailSender == null) {
            log.info("[Email Service] Login notification for {} (JavaMailSender not configured).", toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(toEmail);
            message.setSubject("Successful Login — Welcome to ShoppersClub!");
            message.setText("Hello,\n\nYou have successfully logged into your ShoppersClub account.\n\nThank you for shopping with us!\n\nBest regards,\nShoppersClub Team");

            mailSender.send(message);
            log.info("Login notification email dispatched to: {}", toEmail);
        } catch (Exception ex) {
            log.warn("Could not dispatch login email to {}: {}", toEmail, ex.getMessage());
        }
    }

    @Override
    public void sendOrderPlacedNotification(String toEmail, Long orderId, BigDecimal totalAmount, Integer totalItems) {
        if (mailSender == null) {
            log.info("[Email Service] Order #{} placed notification for {} (JavaMailSender not configured).", orderId, toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(toEmail);
            message.setSubject("Order Confirmed! — ShoppersClub Order #" + orderId);
            message.setText(String.format(
                "Hello,\n\nThank you for your order!\n\nOrder Details:\n- Order ID: #%d\n- Total Items: %d\n- Total Amount: $%s\n- Status: PENDING / PROCESSING\n\nWe are preparing your items for shipment.\n\nBest regards,\nShoppersClub Team",
                orderId, totalItems, totalAmount != null ? totalAmount.toPlainString() : "0.00"
            ));

            mailSender.send(message);
            log.info("Order confirmation email dispatched for Order #{} to: {}", orderId, toEmail);
        } catch (Exception ex) {
            log.warn("Could not dispatch order confirmation email for Order #{} to {}: {}", orderId, toEmail, ex.getMessage());
        }
    }

    @Override
    public void sendOrderShippedNotification(String toEmail, Long orderId) {
        if (mailSender == null) {
            log.info("[Email Service] Order #{} shipped notification for {} (JavaMailSender not configured).", orderId, toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(SENDER_EMAIL);
            message.setTo(toEmail);
            message.setSubject("Your Order #" + orderId + " Has Been Shipped! — ShoppersClub");
            message.setText(String.format(
                "Great news!\n\nYour ShoppersClub Order #%d is now on its way to you!\n\nStatus: SHIPPED\n\nThank you for choosing ShoppersClub!\n\nBest regards,\nShoppersClub Team",
                orderId
            ));

            mailSender.send(message);
            log.info("Order shipped email dispatched for Order #{} to: {}", orderId, toEmail);
        } catch (Exception ex) {
            log.warn("Could not dispatch order shipped email for Order #{} to {}: {}", orderId, toEmail, ex.getMessage());
        }
    }
}
