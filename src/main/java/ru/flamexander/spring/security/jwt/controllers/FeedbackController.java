package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.ContactFormDto;
import ru.flamexander.spring.security.jwt.service.EmailService;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final EmailService emailService;

    public FeedbackController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<?> handleFeedback(@RequestBody ContactFormDto contactForm) {
        try {
            emailService.sendContactEmail(contactForm);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка: " + e.getMessage());
        }
    }
}