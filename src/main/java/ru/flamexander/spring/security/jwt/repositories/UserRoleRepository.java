package ru.flamexander.spring.security.jwt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.flamexander.spring.security.jwt.entities.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    boolean existsByUserIdAndRoleId(Long userId, Integer roleId);
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Integer roleId);
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Integer roleId);

    // Метод для удаления всех ролей пользователя по userId
    void deleteAllByUserId(Long userId);
}
