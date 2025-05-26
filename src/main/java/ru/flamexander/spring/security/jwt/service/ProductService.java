package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.Product;
import ru.flamexander.spring.security.jwt.repositories.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    @Autowired
    public ProductService(ProductRepository productRepository, EntityManager entityManager) {
        this.productRepository = productRepository;
        this.entityManager = entityManager;
    }


    @Transactional
    public boolean decreaseStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    @Transactional
    public void replenishStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.getStock() < product.getMaxStock()) {
            product.setStock(product.getMaxStock());
            productRepository.save(product);
        }
    }

    // Основные методы с пагинацией
    public Page<Product> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> searchProducts(String query, int page, int size) {
        String processedQuery = getWordBase(query); // Приводим к основе слова
        Pageable pageable = PageRequest.of(page - 1, size);
        return productRepository.searchProducts("%" + processedQuery + "%", pageable);
    }

    private String getWordBase(String word) {
        // Простая реализация стемминга для русского языка
        word = word.toLowerCase()
                .replaceAll("(ий|ый|ой|ая|ое|ие|ые|ой|ем|им|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$", "")
                .replaceAll("(иям|ям|ием|ем|ам|ом|ами|ями|иях|ях|ы|ь|ию|ью|ю|я|а|е|о|и|ы|у)$", "");
        return word;
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }


    // Вспомогательные методы
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    @Transactional
    public void clearProductCache() {
        entityManager.clear(); // Очищает весь кэш первого уровня
    }
}