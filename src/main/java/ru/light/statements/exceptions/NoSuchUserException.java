package ru.light.statements.exceptions;

import lombok.Getter;

@Getter
public class NoSuchUserException extends RuntimeException {
    
    private static final String message = "Не существует такого пользователя";

}
