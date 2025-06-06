package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.flamexander.spring.security.jwt.dtos.CartItemDto;
import ru.flamexander.spring.security.jwt.dtos.OrderDto;
import ru.flamexander.spring.security.jwt.entities.Cart;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.entities.Product;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.CartService;
import ru.flamexander.spring.security.jwt.service.OrderService;
import ru.flamexander.spring.security.jwt.service.ProductService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.security.Principal;
import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final ProductService productService;

    public OrderController(OrderService orderService,
                           UserService userService,
                           CartService cartService, ProductService productService) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.productService = productService;
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
            // Проверяем наличие товаров перед оформлением заказа
            for (CartItemDto item : orderDto.getItems()) {
                Product product = productService.getProductById(item.getProductId());
                if (product.getStock() < item.getQuantity()) {
                    redirectAttributes.addFlashAttribute("error",
                            "Недостаточно товара: " + product.getName() +
                                    " (доступно: " + product.getStock() + ")");
                    return "redirect:/checkout";
                }
            }

            Order order = orderService.createOrder(orderDto, principal.getName());
            redirectAttributes.addFlashAttribute("order", order);
            return "redirect:/order-success";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка оформления заказа: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/my")
    public String showUserOrders(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        model.addAttribute("orders", orderService.getOrdersByUser(user));
        return "profile-view";
    }
}