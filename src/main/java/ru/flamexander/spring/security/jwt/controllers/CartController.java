package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.flamexander.spring.security.jwt.entities.Cart;
import ru.flamexander.spring.security.jwt.entities.CartItem;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.CartService;
import ru.flamexander.spring.security.jwt.service.UserService;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    @Autowired
    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping("/cart")
    public String viewCart(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            Cart cart = cartService.getCartForUser(user);
            if (cart != null) {
                List<CartItem> items = cartService.getCartItems(cart);
                model.addAttribute("cartItems", items);
                BigDecimal total = calculateTotal(items);
                model.addAttribute("total", total);
            } else {
                model.addAttribute("cartItems", new ArrayList<>());
                model.addAttribute("total", BigDecimal.ZERO);
            }
        } else {
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("total", BigDecimal.ZERO);
        }
        return "cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String removeItemFromCart(@PathVariable Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            cartService.removeItemFromCart(user, id);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/edit/{id}")
    public String editItemInCart(@PathVariable Long id, @RequestParam(required = false) Integer quantity, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            if (quantity != null) {
                cartService.editItemInCart(user, id, quantity);
            } else {
                // Обработка случая, когда quantity не передан
                return "redirect:/cart";
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/edit/{id}")
    public String editItemInCartPost(@PathVariable Long id, @RequestParam Integer quantity, Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            cartService.editItemInCart(user, id, quantity);
        }
        return "redirect:/cart";
    }


    private BigDecimal calculateTotal(List<CartItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            total = total.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }


}
