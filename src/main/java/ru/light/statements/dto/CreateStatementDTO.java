package ru.light.statements.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStatementDTO {

    public String title;
    public String content;
    public String senderName;
    public String phone;
    
}
