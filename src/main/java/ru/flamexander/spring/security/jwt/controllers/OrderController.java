package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.dtos.OrderDto;
import ru.flamexander.spring.security.jwt.entities.Cart;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.CartService;
import ru.flamexander.spring.security.jwt.service.OrderService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.security.Principal;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;

    public OrderController(OrderService orderService,
                           UserService userService,
                           CartService cartService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Проверяем и инициализируем обязательные поля
        if (user.getCity() == null) user.setCity("");
        if (user.getStreet() == null) user.setStreet("");
        if (user.getHouse() == null) user.setHouse("");
        if (user.getApartment() == null) user.setApartment("");

        OrderDto orderDto = new OrderDto();
        orderDto.setEmail(user.getEmail());
        orderDto.setCity(user.getCity());
        orderDto.setStreet(user.getStreet());
        orderDto.setHouse(user.getHouse());
        orderDto.setApartment(user.getApartment());
        orderDto.setItems(cartService.getCartItemsAsDto(user));

        model.addAttribute("orderDto", orderDto);
        model.addAttribute("user", user); // Убедитесь что передаете user в модель
        return "checkout";
    }

    @PostMapping("/create-order")
    public String createOrder(@ModelAttribute("orderDto") OrderDto orderDto,
                              Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.createOrder(orderDto, principal.getName());
            redirectAttributes.addFlashAttribute("order", order);
            return "redirect:/order-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка оформления: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/order-success")
    public String orderSuccess(@ModelAttribute("order") Order order, Model model) {
        if (order == null || order.getId() == null) {
            return "redirect:/";
        }
        return "order-success";
    }
}