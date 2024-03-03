package ru.light.statements.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.light.statements.dto.CreateStatementDTO;
import ru.light.statements.dto.UpdateStatementDTO;
import ru.light.statements.entities.Statement;
import ru.light.statements.entities.User;
import ru.light.statements.enums.StatementStatus;
import ru.light.statements.enums.UserRole;
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

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getStatement(@PathVariable("id") Long statementId) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        Statement statement = statementService.getStatement(statementId);

        if (statement == null) {
            return new ResponseEntity<>(HttpStatusCode.valueOf(404));
        }
        if (user.getRole().equals(UserRole.OPERATOR) && !statement.getStatus().equals(StatementStatus.SEND) ||
            user.getRole().equals(UserRole.USER) && statement.getSender().getId() != user.getId() ||
            user.getRole().equals(UserRole.ADMIN) && statement.getStatus().equals(StatementStatus.DRAFT)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(statement, HttpStatus.OK);

    }

    @GetMapping("/get")
    public ResponseEntity<?> getStatements(@RequestParam(value = "creation_sort_direct", defaultValue = "ASC") Sort.Direction creationSortDirection,
                                           @RequestParam(value = "name", defaultValue = "") String creatorNamePart,
                                           @RequestParam(value = "status", defaultValue = "ALL") String statementStatusString,
                                           @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                           @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        
        ArrayList<Statement> statementsList = new ArrayList<>();
        switch (user.getRole()) {
            case USER:
                statementsList = statementService.getStatementsByUserId(user.getId(), limit, offset, creationSortDirection, statementStatusString);
                break;
            case OPERATOR:
                statementsList = statementService.getSendStatements(limit, offset, creationSortDirection);
                break;
            case ADMIN:
                statementsList = statementService.getStatements(limit, offset, creationSortDirection, statementStatusString, creatorNamePart);
                break;
        }

        return new ResponseEntity<>(statementsList, HttpStatus.OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<?> createStatement(@RequestBody @Validated CreateStatementDTO dto) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
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
    public ResponseEntity<?> editStatement(@RequestBody @Validated UpdateStatementDTO statement) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        log.info(user.getLogin() + " in put edit");
        try {
            statementService.update(statement);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            log.error("error while editing statement", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/send/{id}")
    public ResponseEntity<?> sendStatement(@PathVariable("id") Long statementId) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        statementService.changeStatus(statementId, StatementStatus.SEND);
        log.info("user " + user.getLogin() + " send statement " + statementId);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PutMapping("/status/accept/{statementId}")
    public ResponseEntity<?> acceptStatement(@PathVariable("statementId") Long statementId) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        statementService.changeStatus(statementId, StatementStatus.ACCEPTED);
        log.info("operator " + user.getLogin() + " accepted statement " + statementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/status/reject/{statementId}")
    public ResponseEntity<?> rejectStatement(@PathVariable("statementId") Long statementId) {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        statementService.changeStatus(statementId, StatementStatus.REJECTED);
        log.info("operator " + user.getLogin() + " rejected statement " + statementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
}
