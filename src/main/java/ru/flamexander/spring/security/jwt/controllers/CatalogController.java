package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.flamexander.spring.security.jwt.entities.Cart;
import ru.flamexander.spring.security.jwt.entities.CartItem;
import ru.flamexander.spring.security.jwt.entities.Product;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.service.CartService;
import ru.flamexander.spring.security.jwt.service.ProductService;
import ru.flamexander.spring.security.jwt.service.UserService;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
public class CatalogController {

    private final ProductService productService;
    private final UserService userService;
    private final CartService cartService;

    public CatalogController(ProductService productService, UserService userService, CartService cartService) {
        this.productService = productService;
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping("/catalog")
    public String showMainCatalog(@RequestParam(required = false) String category,
                                  @RequestParam(required = false) String search,
                                  @RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "9") int size,
                                  Model model,
                                  HttpSession session,
                                  Principal principal) {
        Pageable pageable = PageRequest.of(page - 1, size); // Страницы в Spring начинаются с 0
        Page<Product> productPage;

        if (search != null && !search.isEmpty()) {
            productPage = productService.searchProducts(search, page, size);
            model.addAttribute("searchQuery", search);
        } else if (category != null && !category.isEmpty()) {
            productPage = productService.getProductsByCategory(category, page, size);
        } else {
            productPage = productService.getAllProducts(page, size);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);

        if (principal != null) {
            model.addAttribute("authenticated", true);
        } else {
            model.addAttribute("authenticated", false);
        }

        return "catalog";
    }

    @GetMapping("/catalog/cart")
    public String viewCatalogCart(Model model, HttpSession session, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                Cart cart = cartService.getCartForUser(user);
                if (cart != null) {
                    List<CartItem> cartItems = cartService.getCartItems(cart);
                    if (cartItems.isEmpty()) {
                        model.addAttribute("cartItems", new ArrayList<>());
                        model.addAttribute("total", BigDecimal.ZERO);
                        return "cart";
                    }

                    BigDecimal total = BigDecimal.ZERO;
                    for (CartItem item : cartItems) {
                        total = total.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    }

                    model.addAttribute("cartItems", cartItems);
                    model.addAttribute("total", total);
                    return "cart";
                }
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam(value = "selectedProducts", required = false) List<Long> selectedProducts,
                            HttpSession session, Principal principal) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null && selectedProducts != null) {
                for (Long productId : selectedProducts) {
                    cartService.addItemToCart(user, productId);
                }
            }
        }
        return "redirect:/cart";
    }
}