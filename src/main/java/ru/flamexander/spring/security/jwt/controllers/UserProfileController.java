package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.UserService;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    @Autowired
    private UserService userService;

//    @GetMapping
//    public String getUserProfile(Model model) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userService.findByUsername(username).orElseThrow();
//        model.addAttribute("user", user);
//        return "user-profile";
//    }
//
//    @PostMapping
//    public String updateUserProfile(@ModelAttribute User user) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User existingUser = userService.findByUsername(username).orElseThrow();
//
//        existingUser.setUsername(user.getUsername());
//        existingUser.setEmail(user.getEmail());
//        existingUser.setPassword(user.getPassword()); // Обратите внимание: пароль не должен хешироваться здесь
//
//        // Хеширование пароля должно происходить в сервисе
//        userService.updateUser(existingUser.getId(), existingUser);
//
//        return "redirect:/profile";
//    }
}
