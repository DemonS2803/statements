package ru.light.statements.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.light.statements.entities.User;
import ru.light.statements.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    User findUserByLogin(String login);
    List<User> findUserByLoginLike(String loginPart, PageRequest pageRequest);
    List<User> findUserByLoginLikeAndRole(String loginPart, UserRole role, PageRequest pageRequest);
}
