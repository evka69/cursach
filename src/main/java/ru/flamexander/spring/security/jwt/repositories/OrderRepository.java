package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.flamexander.spring.security.jwt.entities.Order;
import ru.flamexander.spring.security.jwt.entities.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findAllByOrderByCreatedAtDesc();  // для получения всех заказов, отсортированных по дате
}