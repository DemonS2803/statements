package ru.light.statements.exceptions;

import lombok.Getter;

@Getter
public class UpdateStatementError extends RuntimeException {

    private String message = "Ошибка обновления заявки";
    
}
