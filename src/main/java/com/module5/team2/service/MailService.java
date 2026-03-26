package com.module5.team2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void send(List<String> to, String subject, String content) {

        if (to == null || to.isEmpty()) {
            throw new RuntimeException("Danh sách email rỗng");
        }

        SimpleMailMessage message = new SimpleMailMessage();

        // convert List -> String[]
        message.setTo(to.toArray(new String[0]));

        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }
}