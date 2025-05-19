package ru.flamexander.spring.security.jwt.controllers.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.dtos.UserProfileUpdateDto;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.OrderService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.nio.file.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserProfileMvcController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;


    // Страница просмотра профиля
    @GetMapping
    public String viewProfile(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow();

        List<Order> orders = orderService.getOrdersByUsername(username);  // Получаем заказы

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);  // Передаём в модель

        return "profile-view";
    }

    // Страница редактирования профиля
    @GetMapping("/edit")
    public String editProfileForm(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username).orElseThrow();

        UserProfileUpdateDto dto = new UserProfileUpdateDto();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        user.setCity(dto.getCity());
        user.setStreet(dto.getStreet());
        user.setHouse(dto.getHouse());
        user.setApartment(dto.getApartment());


        model.addAttribute("userProfileUpdateDto", dto);
        return "profile-edit"; // шаблон для редактирования профиля

    }

    // Обработка отправки формы редактирования (БЕЗ загрузки файла)
    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute UserProfileUpdateDto updateDto,
                                Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            User updatedUser = userService.updateCurrentUserProfile(username, updateDto);
            return "redirect:/profile"; // после успешного обновления — возвращаемся к просмотру
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userProfileUpdateDto", updateDto);
            return "profile-edit";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обновлении профиля: " + e.getMessage());
            model.addAttribute("userProfileUpdateDto", updateDto);
//            return "profile-edit";
            return "redirect:/login";
        }
    }
    @PostMapping("/delete")
    public String deleteProfile(Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        boolean deleted = userService.deleteByUsername(username);
        if (deleted) {
            SecurityContextHolder.clearContext(); // Выход из системы
            redirectAttributes.addFlashAttribute("success", "Профиль успешно удалён");
            return "redirect:/login"; // Перенаправление на страницу входа
        } else {
            redirectAttributes.addFlashAttribute("error", "Не удалось удалить профиль");
            return "redirect:/profile";
        }
    }


}
