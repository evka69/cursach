package ru.flamexander.spring.security.jwt.controllers.mvc;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.service.OrderService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/orders")
public class OrderMvcController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderMvcController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/{orderId}")
    public String getOrderDetails(@PathVariable Long orderId, Model model, Principal principal) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));

        if (!order.getUser().getUsername().equals(principal.getName())) {
            throw new AccessDeniedException("Нет доступа к этому заказу");
        }

        model.addAttribute("order", order);
        return "order-details";
    }
}
