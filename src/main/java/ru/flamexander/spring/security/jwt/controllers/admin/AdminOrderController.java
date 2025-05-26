package ru.flamexander.spring.security.jwt.controllers.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.OrderService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")  // Перенесем базовый путь сюда
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "admin/orders-list";
    }

    @GetMapping("/user/{userId}")
    public String getUserOrders(@PathVariable Long userId, Model model) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "admin/order-view";
    }

    @GetMapping("/admin/orders")
    public String getAllOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "orders-list";
    }

    @PostMapping("/{id}/update-status")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String newStatus,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(id, newStatus);
            redirectAttributes.addFlashAttribute("success", "Статус заказа обновлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка обновления статуса: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    @GetMapping("/my-orders")
    public String getUserOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Order> orders = orderService.getUserOrders(user);
        model.addAttribute("orders", orders);
        return "profile-view";
    }

    public String getStatusDescription(String status) {
        switch (status) {
            case "NEW": return "Новый";
            case "PROCESSING": return "В обработке";
            case "SHIPPED": return "Отправлен";
            case "DELIVERED": return "Доставлен";
            case "CANCELLED": return "Отменен";
            default: return status;
        }
    }
}