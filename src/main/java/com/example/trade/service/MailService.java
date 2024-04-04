package com.example.trade.service;

import jakarta.annotation.PreDestroy;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    private final ExecutorService executor;

    public MailService() {
        executor = Executors.newFixedThreadPool(3);
    }

    public void createAndSendMimeMessageAsync(String content, String from, String to) {
        executor.submit(() -> {
            createAndSendMimeMessage(content, from, to);
        });
    }

    private void createAndSendMimeMessage(String content, String from, String to) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(from, "Trade Notification");
            helper.setTo(to);
            helper.setSubject("New trade offer");
            helper.setText(content, true); // Enable HTML content
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        emailSender.send(message);
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

}
