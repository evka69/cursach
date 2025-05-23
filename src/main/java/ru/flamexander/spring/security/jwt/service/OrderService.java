package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.dtos.CartItemDto;
import ru.flamexander.spring.security.jwt.dtos.OrderDto;
import ru.flamexander.spring.security.jwt.entities.*;
import ru.flamexander.spring.security.jwt.exceptions.ProductNotFoundException;
import ru.flamexander.spring.security.jwt.repositories.CartItemRepository;
import ru.flamexander.spring.security.jwt.repositories.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final ProductService productService;
    private final EmailService emailService;
    private final CartService cartService;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Transactional
    public Order createOrder(OrderDto orderDto, String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress(formatAddress(orderDto));
        order.setEmail(orderDto.getEmail());
        order.setPaymentMethod(orderDto.getPaymentMethod());
        order.setComment(orderDto.getComment());
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> items = createOrderItems(order, orderDto.getItems());
        order.setItems(items);
        order.setTotalAmount(calculateTotalAmount(items));

        // Уменьшаем количество товаров на складе
        for (OrderItem item : items) {
            productService.decreaseStock(item.getProduct().getId(), item.getQuantity());
        }

        Order savedOrder = orderRepository.save(order);
        sendOrderConfirmationEmail(savedOrder);
        cartService.clearCart(user);

        return savedOrder;
    }


    private String formatAddress(OrderDto orderDto) {
        return String.format("%s, %s, д.%s%s",
                orderDto.getCity(),
                orderDto.getStreet(),
                orderDto.getHouse(),
                orderDto.getApartment() != null ? ", кв." + orderDto.getApartment() : "");
    }

    private List<OrderItem> createOrderItems(Order order, List<CartItemDto> items) {
        return items.stream()
                .map(item -> {
                    Product product = productService.findById(item.getProductId())
                            .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(product.getPrice());
                    orderItem.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    return orderItem;
                })
                .collect(Collectors.toList());
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void sendOrderConfirmationEmail(Order order) {
        try {
            String subject = "Ваш заказ #" + order.getId() + " успешно оформлен";
            String text = buildEmailText(order);
            emailService.sendOrderEmail(order.getEmail(), subject, text);
            logger.info("Order confirmation email sent to {}", order.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send order confirmation email to {}", order.getEmail(), e);
        }
    }

    private String buildEmailText(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Спасибо за ваш заказ!</h1>");
        sb.append("<p>Номер заказа: <strong>").append(order.getId()).append("</strong></p>");
        sb.append("<p>Дата: ").append(order.getCreatedAt()).append("</p>");
        sb.append("<h2>Детали заказа:</h2>");
        sb.append("<ul>");

        order.getItems().forEach(item ->
                sb.append("<li>")
                        .append(item.getProduct().getName())
                        .append(" - ").append(item.getQuantity()).append(" шт. x ")
                        .append(item.getPrice()).append(" = ")
                        .append(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .append("</li>")
        );

        sb.append("</ul>");
        sb.append("<p><strong>Итого: ").append(order.getTotalAmount()).append("</strong></p>");
        sb.append("<p>Адрес доставки: ").append(order.getDeliveryAddress()).append("</p>");
        sb.append("<p>Способ оплаты: ").append(getPaymentMethodName(order.getPaymentMethod())).append("</p>");

        return sb.toString();
    }

    private String getPaymentMethodName(String method) {
        return method.equals("CASH") ? "Наличными при получении" : "Картой при получении";
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByUsername(String username) {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return orderRepository.findByUser(user);
    }
    @Transactional(readOnly = true)
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
    @Transactional(readOnly = true)
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

}