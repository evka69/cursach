package ru.flamexander.spring.security.jwt.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.dtos.CartItemDto;
import ru.flamexander.spring.security.jwt.entities.Cart;
import ru.flamexander.spring.security.jwt.entities.CartItem;
import ru.flamexander.spring.security.jwt.entities.Product;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.CartItemRepository;
import ru.flamexander.spring.security.jwt.repositories.CartRepository;
import ru.flamexander.spring.security.jwt.repositories.ProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository,
                       UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }

    public List<CartItem> getCartItems(Cart cart) {
        return cartItemRepository.findByCart(cart);
    }

    public List<CartItemDto> getCartItemsAsDto(User user) {
        Cart cart = getCartForUser(user);
        return cartItemRepository.findByCart(cart).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private CartItemDto convertToDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getProduct().getPrice());
        return dto;
    }

    public void addItemToCart(User user, Long productId) {
        Cart cart = getCartForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
        }
    }

    public void removeItemFromCart(User user, Long productId) {
        Cart cart = getCartForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cartItemRepository.findByCartAndProduct(cart, product).ifPresent(item -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartItemRepository.save(item);
            } else {
                cartItemRepository.delete(item);
            }
        });
    }

    public void editItemInCart(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > 10) { // Проверяем лимит
            throw new IllegalStateException("Максимальное количество товара в корзине - 10 шт.");
        }

        Cart cart = getCartForUser(user);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        cartItemRepository.findByCartAndProduct(cart, product).ifPresentOrElse(
                item -> {
                    item.setQuantity(quantity);
                    cartItemRepository.save(item);
                },
                () -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(quantity);
                    cartItemRepository.save(newItem);
                }
        );
    }

    public Map<Long, Integer> getCartItemsMap(User user) {
        Cart cart = getCartForUser(user);
        Map<Long, Integer> result = new HashMap<>();

        cartItemRepository.findByCart(cart).forEach(item ->
                result.put(item.getProduct().getId(), item.getQuantity())
        );

        return result;
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getCartForUser(user);
        cartItemRepository.deleteByCart(cart);
    }

    @Transactional
    public void clearCurrentUserCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        clearCart(user);
    }
}