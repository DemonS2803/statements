package ru.light.statements.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import ru.light.statements.dto.AuthRequestDTO;
import ru.light.statements.dto.AuthResponceDTO;
import ru.light.statements.entities.User;
import ru.light.statements.security.JwtUtils;
import ru.light.statements.services.UserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.time.access-expired}")
    private Integer accessTokenExpirationTime;
    @Value("${jwt.time.refresh-expired}")
    private Integer refreshTokenExpirationTime;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody @Validated AuthRequestDTO loginRequest) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        try {
            User user = userService.getUserByLogin(loginRequest.getLogin());

            authenticateUser(loginRequest.getLogin(), DigestUtils.sha256Hex(loginRequest.getPassword()));
            String accessJwt = jwtUtils.generateJwtToken(user, accessTokenExpirationTime);
            String refreshJwt = jwtUtils.generateJwtToken(user, refreshTokenExpirationTime);
            userService.updateUserRefreshToken(user, refreshJwt);

            log.info("success login with credentials: " + loginRequest);
            return new ResponseEntity<>(new AuthResponceDTO(accessJwt), HttpStatus.ACCEPTED);

        } catch (Exception e) { 
            log.warn("invalid login with credentials: " + loginRequest, e);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    public Authentication authenticateUser(String login, String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @PutMapping("/refresh_token")
    public ResponseEntity<?> refreshToken() {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        
        if (jwtUtils.validateJwtToken(user.getRefreshToken())) {
            log.info(user.getLogin() + " successfully refreshed token");
            String jwt = jwtUtils.generateJwtToken(user, accessTokenExpirationTime);
            return new ResponseEntity<>(new AuthResponceDTO(jwt), HttpStatus.OK);
        }

        log.warn("refresh token has expired");
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    

    // не до конца понял смысл этой кнопки, но сделал так, что она чистит refresh
    // c access токеном ничего не поделать. Когда-нибудь истечет
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        User user = userService.getUserByLogin(((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
        user.setRefreshToken("");
        userService.saveUser(user);
        log.info("user " + user.getLogin() + " logged out");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/hi")
    public ResponseEntity<?> hello() {
        log.info("HELLO WORLD!");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
