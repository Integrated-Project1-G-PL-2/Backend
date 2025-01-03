package com.itbangmodkradankanbanapi.db1.v3.service;

import com.itbangmodkradankanbanapi.db1.config.EmailConfig;
import com.itbangmodkradankanbanapi.db1.v3.dto.CollabDTOResponse;
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
    public void sendEmail(String to, String subject, String body, String sendFrom, String replyTo, CollabDTOResponse collabDTOResponse) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);
            helper.setReplyTo(replyTo);
            helper.setFrom("noreply@intproj23.sit.kmutt.ac.th", sendFrom);
            mailSender.send(message);
            collabDTOResponse.setMailStatus(true);
        } catch (Exception e) {
            collabDTOResponse.setMailStatus(false);
        }
    }

}
