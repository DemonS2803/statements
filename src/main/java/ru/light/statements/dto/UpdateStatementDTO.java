package ru.light.statements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatementDTO {
    
    private Long id;
    private String title;
    private String content;
    private String senderName;
    private String phone;
    
}
