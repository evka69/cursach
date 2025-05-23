package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
            List<CartItem> items = cartService.getCartItemsForUser(user);
            model.addAttribute("cartItems", items);
            model.addAttribute("total", calculateTotal(items));
        } else {
            model.addAttribute("cartItems", new ArrayList<>());
            model.addAttribute("total", BigDecimal.ZERO);
        }
        return "cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeItemFromCart(@PathVariable Long productId,
                                     Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            cartService.removeItemFromCart(user, productId);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/edit/{id}")
    public String editItemInCart(@PathVariable Long id,
                                 @RequestParam(required = false) Integer quantity,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) { // Добавляем RedirectAttributes
        try {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null && quantity != null) {
                cartService.editItemInCart(user, id, quantity);
            }
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
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
