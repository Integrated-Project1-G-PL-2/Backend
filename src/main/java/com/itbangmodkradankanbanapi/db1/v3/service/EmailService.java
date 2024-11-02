package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.config.EmailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String body, String sendFrom) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            helper.setFrom("eiei@gmail.com", sendFrom);

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
