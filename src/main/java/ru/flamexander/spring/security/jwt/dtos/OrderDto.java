package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;
import java.util.List;

@Data
public class OrderDto {
    private String city;
    private String street;
    private String house;
    private String apartment;
    private String email;
    private String paymentMethod;
    private List<CartItemDto> items;
    private String comment;
}