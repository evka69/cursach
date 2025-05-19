package ru.flamexander.spring.security.jwt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.flamexander.spring.security.jwt.dtos.RegistrationUserDto;
import ru.flamexander.spring.security.jwt.dtos.UserDto;
import ru.flamexander.spring.security.jwt.dtos.UserProfileUpdateDto;
import ru.flamexander.spring.security.jwt.entities.User;
import ru.flamexander.spring.security.jwt.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleService roleService,
                       UserRoleService userRoleService,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByUsername(username));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден", username)
        ));

        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));

        return userRoleService.createUserWithRole(user, roleService.getRoleIdByName("ROLE_USER"));
    }

    public boolean deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User userToUpdate = optionalUser.get();

            if (userDetails.getUsername() != null) {
                userToUpdate.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null) {
                userToUpdate.setEmail(userDetails.getEmail());
            }
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }

            return userRepository.save(userToUpdate);
        }
        return null; // Пользователь не найден
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Transactional
    public boolean deleteByUsername(String username) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            userRepository.delete(userOpt.get());
            return true;
        }
        return false;
    }

    /**
     * Обновление профиля текущего пользователя.
     * Смена пароля необязательна: если новый пароль не введён, пароль не меняется.
     */
    public User updateCurrentUserProfile(String username, UserProfileUpdateDto updateDto) {
        Optional<User> optionalUser = findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Пользователь не найден");
        }

        User user = optionalUser.get();

        boolean isChangingPassword = updateDto.getNewPassword() != null && !updateDto.getNewPassword().isEmpty();

        if (isChangingPassword) {
            if (updateDto.getOldPassword() == null || updateDto.getOldPassword().isEmpty()) {
                throw new IllegalArgumentException("Старый пароль обязателен для смены пароля");
            }
            if (!passwordEncoder.matches(updateDto.getOldPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Старый пароль неверный");
            }
            if (updateDto.getConfirmPassword() == null || !updateDto.getNewPassword().equals(updateDto.getConfirmPassword())) {
                throw new IllegalArgumentException("Новый пароль и подтверждение не совпадают");
            }
            user.setPassword(passwordEncoder.encode(updateDto.getNewPassword()));
        }

        if (updateDto.getUsername() != null) {
            user.setUsername(updateDto.getUsername());
        }

        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }

        user.setCity(updateDto.getCity());
        user.setStreet(updateDto.getStreet());
        user.setHouse(updateDto.getHouse());
        user.setApartment(updateDto.getApartment());

        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
