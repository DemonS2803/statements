package ru.light.statements.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneDataDTO {

    private String source;
    private String type;
    private String phone;
    private Integer countryCode;
    private Integer cityCode;
    private Integer number;
    private Integer extension;
    private String provider;
    private String country;
    private String region;
    private String city;
    private String timezone;
    private Integer qcConflict;
    private Integer qc;
    
}
