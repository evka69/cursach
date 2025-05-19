package ru.flamexander.spring.security.jwt.entities;

import lombok.Data;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
@Data // если используете Lombok
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", unique = true)
    private String title;

    @Column(name = "description")
    private String description;


}