package com.hr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String employeeName, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to our company - Your Account Details");
            
            String emailBody = String.format(
                "Dear %s,\n\n" +
                "Welcome to our company! We are excited to have you join our team.\n\n" +
                "Your account has been successfully created with the following details:\n" +
                "Email: %s\n" +
                "Password: %s\n\n" +
                "Please login to your account and change your password for security purposes.\n\n" +
                "If you have any questions, please don't hesitate to contact the HR department.\n\n" +
                "Best regards,\n" +
                "HR-Team",
                employeeName, toEmail, password
            );
            
            message.setText(emailBody);
            javaMailSender.send(message);
            
            System.out.println("Welcome email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send welcome email to: " + toEmail);
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
