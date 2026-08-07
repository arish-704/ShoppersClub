package com.arish.shoppersclub.service;

/**
 * Service interface for sending email notifications.
 */
public interface EmailService {

    /**
     * Sends a simple login notification email to the user upon successful authentication.
     *
     * @param toEmail Recipient email address
     */
    void sendLoginNotification(String toEmail);

}
