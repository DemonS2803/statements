package ru.light.statements.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.light.statements.entities.User;
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

    

}
