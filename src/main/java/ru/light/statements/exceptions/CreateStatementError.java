package ru.light.statements.exceptions;

import lombok.Getter;

@Getter
public class CreateStatementError extends RuntimeException {

    private String message = "Ошибка создания заявки";
    
}
