package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String deliveryAddress;
    private String email;
    private String paymentMethod; // "CARD" или "CASH"
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String comment; // Добавленное поле


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

    @Column(name = "status")
    private String status;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "NEW";  // статус по умолчанию при создании заказа
        }
    }
}
