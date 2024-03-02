package ru.light.statements.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import ru.light.statements.api.DadataAPI;
import ru.light.statements.dto.CreateStatementDTO;
import ru.light.statements.entities.Statement;
import ru.light.statements.entities.User;
import ru.light.statements.enums.StatementStatus;
import ru.light.statements.exceptions.CreateStatementError;
import ru.light.statements.exceptions.UpdateStatementError;
import ru.light.statements.repositories.StatementRepository;

@Slf4j
@Service
public class StatementService {

    @Autowired
    private StatementRepository statementRepository;
    @Autowired
    private DadataAPI dadataAPI;

    public void create(CreateStatementDTO dto, User user) throws JsonMappingException, JsonProcessingException {
        String phone = dto.getPhone() == null ? user.getPhone() : dto.getPhone();
        String data = dadataAPI.getPhoneData(DadataAPI.dadataToken, DadataAPI.dadataSecret, "[" + phone + "]");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(data);
        JsonNode node = mapper.readTree(data.substring(1, data.length() - 1));
        log.info(data.substring(1, data.length() - 1));
        if (node.get("phone").isEmpty()) {
            throw new CreateStatementError();
        }

        Statement statement = Statement.builder()
                                       .title(dto.title)
                                       .content(dto.content)
                                       .status(StatementStatus.SEND)
                                       .sender(user)
                                       .created(LocalDateTime.now())
                                       .phone(node.get("phone").asText())
                                       .countryCode(node.get("country_code").asInt())
                                       .cityCode(node.get("city_code").asInt())
                                       .build();
        
        statementRepository.save(statement);
    }

    public void update(Statement statement) {
        if (!statement.getStatus().equals(StatementStatus.DRAFT)) {
            log.error("error while updatint statement " + statement);
            throw new UpdateStatementError();
        }
            
        statementRepository.save(statement);
        log.info("statement " + statement.getId() + " was updated");
    }
    
    // public List<Statement> getUserStatements
}
