package com.arish.shoppersclub.service;

import java.math.BigDecimal;

/**
 * Service interface for dispatching email notifications.
 */
public interface EmailService {

    /**
     * Sends a simple login notification email to the user upon successful authentication.
     *
     * @param toEmail Recipient email address
     */
    void sendLoginNotification(String toEmail);

    /**
     * Sends an order placement confirmation email when an order is placed.
     *
     * @param toEmail Recipient email address
     * @param orderId Order ID
     * @param totalAmount Order total amount
     * @param totalItems Number of items in order
     */
    void sendOrderPlacedNotification(String toEmail, Long orderId, BigDecimal totalAmount, Integer totalItems);

    /**
     * Sends an email notification when an order status is updated to SHIPPED.
     *
     * @param toEmail Recipient email address
     * @param orderId Order ID
     */
    void sendOrderShippedNotification(String toEmail, Long orderId);
}
