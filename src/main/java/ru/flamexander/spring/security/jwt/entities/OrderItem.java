package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    private Product product;
    private Integer quantity;
    private BigDecimal price;

    @Column(name = "total_price")
    private BigDecimal totalPrice;
}
