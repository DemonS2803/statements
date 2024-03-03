package ru.light.statements.services;


import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.light.statements.entities.Statement;
import ru.light.statements.entities.User;
import ru.light.statements.enums.UserRole;
import ru.light.statements.exceptions.NoSuchUserException;
import ru.light.statements.repositories.UserRepository;
import ru.light.statements.security.JwtUtils;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    

    public User getUserByLogin(String login) {
        return userRepository.findUserByLogin(login);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).get();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserFromToken(String token) {
        User user = getUserByLogin(jwtUtils.getUserLoginFromToken(token));
        return user;
    }

    public void updateUserRefreshToken(User user, String newToken) {
        user.setRefreshToken(newToken);
        userRepository.save(user);
    }

    public void makeUserOperator(Long userId) {
        User user = userRepository.getReferenceById(userId);
        if (user == null) throw new NoSuchUserException();

        user.setRole(UserRole.OPERATOR);
        userRepository.save(user);
    }

    public void makeUserUsual(Long userId) {
        User user = userRepository.getReferenceById(userId);
        if (user == null) throw new NoSuchUserException();

        user.setRole(UserRole.USER);
        userRepository.save(user);
    }

    public ArrayList<User> getUsers(Integer limit, 
        Integer offset, 
        Sort.Direction sortDirection,
        String role,
        String userLoginPart) {
            PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(sortDirection, "login"));
            if ("ALL".equals(role)) {
                return (ArrayList<User>) userRepository.findUserByLoginLike("%" + userLoginPart + "%", pageRequest);
            }
            return (ArrayList<User>) userRepository.findUserByLoginLikeAndRole("%" + userLoginPart + "%", UserRole.valueOf(role), pageRequest);
        }

    public boolean hasUserPermissionToEdit(User user, Statement statement) {
        return statement.getSender().getId() == user.getId();
    }

}
