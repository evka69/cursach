package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.dtos.UserDto;
import ru.flamexander.spring.security.jwt.dtos.UserProfileUpdateDto;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.OrderService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    private OrderService orderService;

    // Обновление профиля текущего пользователя с проверкой пароля
//    @PutMapping("/profile")
//    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody UserProfileUpdateDto updateDto) {
//        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//        User updatedUser = userService.updateCurrentUserProfile(currentUsername, updateDto);
//        if (updatedUser == null) {
//            return ResponseEntity.badRequest().body("Ошибка обновления профиля: неверный старый пароль или данные");
//        }
//        return ResponseEntity.ok(updatedUser);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        boolean isDeleted = userService.deleteById(id);
        if (isDeleted) {
            return ResponseEntity.noContent().build(); // Возвращаем 204 No Content, если удаление прошло успешно
        } else {
            return ResponseEntity.notFound().build(); // Возвращаем 404 Not Found, если пользователь не найден
        }
    }
    @GetMapping
    public String getProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Order> orders = orderService.getOrdersByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);

        return "profile-view"; // Ваш шаблон профиля
    }
}
