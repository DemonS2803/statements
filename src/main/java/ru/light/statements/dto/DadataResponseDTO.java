package ru.light.statements.dto;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DadataResponseDTO {
    
    ArrayList<PhoneDataDTO> list;
}
