package com.example.app.modules.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String body) {
        mailService.sendEmail(to, subject, body);
        return ResponseEntity.ok("Mail sent successfully");
    }
}
