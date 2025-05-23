package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.flamexander.spring.security.jwt.entities.Role;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.RoleRepository;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRoleService userRoleService;

    @Override
    public void run(String... args) throws Exception {
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("password"));

            Role adminRole = roleService.getRoleByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_ADMIN");
                        return roleRepository.save(newRole);
                    });

            // Создаем админа с ролью через UserRoleService
            userRoleService.createUserWithRole(admin, roleService.getRoleIdByName("ROLE_ADMIN"));
        }
    }

    private void initUser() {
        if (!userService.existsByUsername("user1")) {
            User user = new User();
            user.setUsername("user1");
            user.setEmail("user1@example.com");
            user.setPassword(passwordEncoder.encode("123QWEasd!"));

            Role userRole = roleService.getRoleByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });

            userRoleService.createUserWithRole(user, roleService.getRoleIdByName("ROLE_USER"));
        }
    }

    private void initRealUsers() {
        createRealUser("ivan_petrov", "ivan.petrov@example.com", "Казань",
                "Мира", "9", "12");
        createRealUser("anna_smirnova", "anna.smirnova@example.com", "Альметьевск",
                "Толстова", "78", "232");
        createRealUser("sergey_ivanov", "sergey.ivanov@example.com", "Москва",
                "Чернышевского", "6", "54");
        createRealUser("elena_kuznetsova", "elena.kuznetsova@example.com", "Челябинск",
                "Белоглазова", "87", "1");
        createRealUser("dmitry_sokolov", "dmitry.sokolov@example.com", "Сочи",
                "Пушкина", "2", "4");
        createRealUser("olga_vorobeva", "olga.vorobeva@example.com", "Анапа",
                "Аминова", "12", "54");
        createRealUser("alexey_fedorov",  "alexey.fedorov@example.com", "Краснодар",
                "Сулеймановой", "43", "65");
        createRealUser("maria_volkova", "maria.volkova@example.com", "Тюмень",
                "Чехова", "34", "76");
        createRealUser("andrey_morozov", "andrey.morozov@example.com", "Керчь",
                "Шевченко", "3", "87");
        createRealUser("ekaterina_pavlova",  "ekaterina.pavlova@example.com", "Нефтеюганск",
                "Ленина", "5", "6");
    }

    private void createRealUser(String username, String email, String city,
                                String street, String house, String apartment) {
        if (!userService.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setCity(city);
            user.setStreet(street);
            user.setHouse(house);
            user.setApartment(apartment);
            user.setPassword(passwordEncoder.encode("PAssword123!")); // Стандартный пароль для всех тестовых пользователей

            Role userRole = roleService.getRoleByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_USER");
                        return roleRepository.save(newRole);
                    });

            userRoleService.createUserWithRole(user, roleService.getRoleIdByName("ROLE_USER"));
        }
    }

}
