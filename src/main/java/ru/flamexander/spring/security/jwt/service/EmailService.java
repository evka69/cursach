package ru.flamexander.spring.security.jwt.service;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.dtos.ContactFormDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.fromEmail = mailSender instanceof org.springframework.mail.javamail.JavaMailSenderImpl ?
                ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).getUsername() :
                "noreply@yourdomain.com";
    }

    // Метод для формы обратной связи (оставлен без изменений)
    public void sendContactEmail(ContactFormDto formData) {
        try {
            logger.info("Attempting to send contact email from {} to blinn2006@mail.ru", fromEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo("blinn2006@mail.ru");
            helper.setSubject("New contact form submission: " + formData.getName());

            String text = String.format(
                    "Name: %s\nEmail: %s\nPhone: %s\n\nMessage:\n%s",
                    formData.getName(),
                    formData.getEmail(),
                    formData.getPhone() != null ? formData.getPhone() : "Not provided",
                    formData.getMessage()
            );

            helper.setText(text);

            mailSender.send(message);
            logger.info("Contact email sent successfully!");

        } catch (MailAuthenticationException e) {
            logger.error("SMTP Authentication failed. Check your credentials.", e);
            throw new RuntimeException("Mail server authentication failed", e);
        } catch (MailSendException e) {
            logger.error("Failed to send email. SMTP server response: {}", e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            logger.error("Unexpected error during email sending", e);
            throw new RuntimeException("Email sending error", e);
        }
    }

    // Улучшенный метод для отправки уведомлений о заказе
    public void sendOrderEmail(String toEmail, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true указывает на HTML-формат

            mailSender.send(message);
            logger.info("Order confirmation email sent to {}", toEmail);
        } catch (MailAuthenticationException e) {
            logger.error("SMTP Authentication failed while sending order confirmation", e);
            throw new RuntimeException("Mail server authentication failed", e);
        } catch (MessagingException e) {
            logger.error("Failed to create order confirmation email", e);
            throw new RuntimeException("Email creation failed", e);
        } catch (MailSendException e) {
            logger.error("Failed to send order confirmation. SMTP server response: {}", e.getMessage());
            throw new RuntimeException("Email sending failed", e);
        } catch (Exception e) {
            logger.error("Unexpected error during order email sending", e);
            throw new RuntimeException("Order email sending error", e);
        }
    }

    // Дополнительный метод для отправки простых текстовых email
    public void sendSimpleEmail(String toEmail, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(text);

            mailSender.send(message);
            logger.info("Simple email sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send simple email", e);
            throw new RuntimeException("Simple email sending failed", e);
        }
    }
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Восстановление пароля");

            String htmlContent = "<html><body>" +
                    "<p>Вы запросили сброс пароля для вашей учетной записи.</p>" +
                    "<p>Для установки нового пароля перейдите по ссылке:</p>" +
                    "<p><a href=\"" + resetLink + "\">" + resetLink + "</a></p>" +
                    "<p>Если вы не запрашивали сброс пароля, проигнорируйте это письмо.</p>" +
                    "</body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("Password reset email sent to {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email", e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

}