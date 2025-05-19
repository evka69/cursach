package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.CartItem;
import ru.flamexander.spring.security.jwt.entities.Cart;
import ru.flamexander.spring.security.jwt.entities.Product;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    @Modifying
    @Transactional
    void deleteByCart(Cart cart); // Добавлено

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.product.id = :productId")
    @Transactional
    void deleteByProductId(@Param("productId") Long productId);
}
