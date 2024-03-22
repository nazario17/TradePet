package com.example.trade.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    public void createAndSendMimeMessage(String content, String from, String to) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(from, "Trade Notification");
            helper.setTo(to);
            helper.setSubject("New trade offer");
            helper.setText(content, true); // Enable HTML content
        } catch (
                MessagingException e) {
            e.printStackTrace();
        } catch (
                UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        emailSender.send(message);
    }
}