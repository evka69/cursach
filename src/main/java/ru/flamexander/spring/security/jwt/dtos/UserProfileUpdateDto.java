package ru.flamexander.spring.security.jwt.dtos;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class UserProfileUpdateDto {

    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    private String city;
    private String street;
    private String house;
    private String apartment;

    private String oldPassword;

    @Pattern(regexp = "^(|(?=.*[A-Za-z])(?=.*\\d).{8,})$", message = "Пароль должен содержать минимум 8 символов, включая буквы и цифры")
    private String newPassword;

    private String confirmPassword;
}

