package ru.flamexander.spring.security.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import ru.flamexander.spring.security.jwt.entities.Product;

@Service
@RequiredArgsConstructor
public class ProductUpdateService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendProductUpdate(Product product) {
        messagingTemplate.convertAndSend("/topic/products", product);
    }
}