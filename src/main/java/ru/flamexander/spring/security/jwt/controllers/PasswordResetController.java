package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.service.PasswordResetService;

@Controller
@RequestMapping("/password-reset")
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/request")
    public String showRequestForm() {
        return "password-reset-request";
    }

    @PostMapping("/request")
    public String processRequest(@RequestParam String email, RedirectAttributes redirectAttributes) {
        try {
            passwordResetService.processPasswordResetRequest(email);
            redirectAttributes.addFlashAttribute("message", "Ссылка для сброса пароля отправлена на ваш email");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/password-reset/request";
    }

    @GetMapping("/reset")
    public String showResetForm(@RequestParam String token, Model model) {
        model.addAttribute("token", token);
        return "password-reset-form";
    }

    @PostMapping("/reset")
    public String processReset(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают");
            return "redirect:/password-reset/reset?token=" + token;
        }

        try {
            passwordResetService.resetPassword(token, password);
            redirectAttributes.addFlashAttribute("message", "Пароль успешно изменен");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/password-reset/reset?token=" + token;
        }
    }
}