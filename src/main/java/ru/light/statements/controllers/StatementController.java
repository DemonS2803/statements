package ru.light.statements.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.light.statements.dto.CreateStatementDTO;
import ru.light.statements.entities.Statement;
import ru.light.statements.entities.User;
import ru.light.statements.services.StatementService;
import ru.light.statements.services.UserService;

@Slf4j
@RestController
@RequestMapping("/api/statement")
public class StatementController {

    @Autowired
    private UserService userService;
    @Autowired
    private StatementService statementService;


    // @GetMapping("/get")
    // public ResponseEntity<?> getStatements() {
        // User user
    // }

    @PostMapping("/edit")
    public ResponseEntity<?> createStatement(@RequestHeader("Authorization") String token, 
                                             @RequestBody @Validated CreateStatementDTO dto) {
        User user = userService.getUserFromToken(token);
        log.info(user.getLogin() + " in post edit");
        try {
            statementService.create(dto, user);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("error while creating statement " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        
    }

    @PutMapping("/edit") 
    public ResponseEntity<?> editStatement(@RequestHeader("Authorization") String token, 
                                            @RequestBody @Validated Statement statement) {
        User user = userService.getUserFromToken(token);
        log.info(user.getLogin() + " in put edit");
        try {
            statementService.update(statement);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("error while editing statement", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        
        
    
    }
    
}
