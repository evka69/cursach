package ru.flamexander.spring.security.jwt.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactsController {

    @GetMapping("/contacts")
    public String showContacts(Model model) {
        // Добавляем данные, которые будут использоваться в шаблоне
        model.addAttribute("companyName", "Интернет-магазин GoodFood");
        model.addAttribute("address", "г. Альметьевск, ул. Мира, д. 9");
        model.addAttribute("phone", "+7 (800) 123-45-67");
        model.addAttribute("email", "info@exampleshop.ru");
        model.addAttribute("workHours", "Пн-Вс: 8:00-23:00, без выходных");

        return "contacts";
    }
}