package ru.light.statements.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.light.statements.entities.User;
import ru.light.statements.enums.UserRole;
import ru.light.statements.services.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> hi() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/get_users")
    public ResponseEntity<?> getUsers(@RequestParam(value = "sort_direct", defaultValue = "ASC") Sort.Direction sortDirection,
                                      @RequestParam(value = "login", defaultValue = "") String userLoginPart,
                                      @RequestParam(value = "role", defaultValue = "ALL") String userRoleString,
                                      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit", defaultValue = "5") Integer limit) {

        ArrayList<User> list = userService.getUsers(limit, offset, sortDirection, userRoleString, userLoginPart);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PutMapping("/make_operator/{userId}")
    public ResponseEntity<?> makeUserOperatorById(@PathVariable("userId") Long userId) {
        // User authorizedUser = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        User newOperatorUser = userService.getUserById(userId);
        if (newOperatorUser.getRole().equals(UserRole.ADMIN)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.makeUserOperator(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/make_usual/{userId}")
    public ResponseEntity<?> makeUserUsualById(@PathVariable("userId") Long userId) {
        // User authorizedUser = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        User newOperatorUser = userService.getUserById(userId);
        if (newOperatorUser.getRole().equals(UserRole.ADMIN)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        userService.makeUserUsual(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
