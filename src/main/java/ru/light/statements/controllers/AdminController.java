package ru.light.statements.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import ru.light.statements.api.DadataAPI;
import ru.light.statements.dto.DadataResponseDTO;
import ru.light.statements.dto.PhoneDataDTO;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private DadataAPI dadataAPI;
    
    @GetMapping("/")
    public ResponseEntity<?> getAdminPage() throws JsonMappingException, JsonProcessingException {
        log.info("opened admin page");
        String data = dadataAPI.getPhoneData(dadataAPI.dadataToken, dadataAPI.dadataSecret, "[89171481185]");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(data);
        System.out.println(data.substring(1, data.length() - 1));
        JsonNode node = mapper.readTree(data.substring(1, data.length() - 1));
        System.out.println(node);
        System.out.println(node.get("country_code"));
        System.out.println(node.get("city_code"));
        System.out.println(node.get("phone"));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
