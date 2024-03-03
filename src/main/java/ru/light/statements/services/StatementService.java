package ru.light.statements.services;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import ru.light.statements.api.DadataAPI;
import ru.light.statements.dto.CreateStatementDTO;
import ru.light.statements.dto.UpdateStatementDTO;
import ru.light.statements.entities.Statement;
import ru.light.statements.entities.User;
import ru.light.statements.enums.StatementStatus;
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
        JsonNode node = mapper.readTree(data.substring(1, data.length() - 1));
        log.info(data.substring(1, data.length() - 1));
        // if (node.get("phone").isEmpty()) {
        //     throw new CreateStatementError();
        // }
        Statement statement = Statement.builder()
                                       .title(dto.getTitle())
                                       .content(dto.getContent())
                                       .status(StatementStatus.DRAFT)
                                       .sender(user)
                                       .senderName(dto.getSenderName() != null ? dto.getSenderName() :  user.getFirstName() + " " + user.getLastName())
                                       .created(LocalDateTime.now())
                                       .phone(node.get("phone").asText())
                                       .countryCode(node.get("country_code").asInt())
                                       .cityCode(node.get("city_code").asInt())
                                       .build();
        
        statementRepository.save(statement);
    }

    public void update(UpdateStatementDTO dto) throws JsonMappingException, JsonProcessingException {
        Statement statement = statementRepository.findStatementById(dto.getId());
        if (!statement.getStatus().equals(StatementStatus.DRAFT)) {
            log.error("error while updating statement " + statement);
            throw new UpdateStatementError();
        }
        System.out.println(dto);
        statement.setTitle(dto.getTitle());
        statement.setContent(dto.getContent());
        statement.setSenderName(dto.getSenderName());
        
        if (dto.getPhone() != null) {

            // номера с +7 он почему то в ошибку выкидывает
            if (dto.getPhone().startsWith("+7")) {
                dto.setPhone(dto.getPhone().replace("+7", "8"));
            }
            String data = dadataAPI.getPhoneData(DadataAPI.dadataToken, DadataAPI.dadataSecret, "[" + dto.getPhone() + "]");
            ObjectMapper mapper = new ObjectMapper();
            System.out.println(data);
            JsonNode node = mapper.readTree(data.substring(1, data.length() - 1));
            log.info(data.substring(1, data.length() - 1));
            statement.setPhone(node.get("phone").asText());
            statement.setCountryCode(node.get("country_code").asInt());
            statement.setCityCode(node.get("city_code").asInt());
        }
        
            
        statementRepository.save(statement);
        log.info("statement " + statement.getId() + " was updated");
    }
    
    public Statement getStatement(Long statementId) {
        return statementRepository.findStatementById(statementId);
    }
    
    public ArrayList<Statement> getStatementsByUserId(Long userId, 
                                                      Integer limit, 
                                                      Integer offset, 
                                                      Sort.Direction sortDirection,
                                                      String status) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(sortDirection, "created"));
        if ("ALL".equals(status)) {
            return (ArrayList<Statement>) statementRepository.findStatementBySenderId(userId, pageRequest);
        }
        return (ArrayList<Statement>) statementRepository.findStatementBySenderIdAndStatus(userId, StatementStatus.valueOf(status), pageRequest);
    }

    public ArrayList<Statement> getSendStatements(Integer limit, 
                                                  Integer offset, 
                                                  Sort.Direction sortDirection,
                                                  String userNamePart) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(sortDirection, "created"));
        return (ArrayList<Statement>) statementRepository.findStatementByStatusAndSenderNameLike(StatementStatus.SEND, "%" + userNamePart +"%", pageRequest);
    }

    public ArrayList<Statement> getStatements(Integer limit, 
                                              Integer offset, 
                                              Sort.Direction sortDirection,
                                              String status,
                                              String creatorNamePart) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(sortDirection, "created"));
        log.info(creatorNamePart);
        if ("ALL".equals(status)) {
            return (ArrayList<Statement>) statementRepository.findStatementBySenderNameLike("%" + creatorNamePart + "%", pageRequest);
        }
        return (ArrayList<Statement>) statementRepository.findStatementByStatusAndSenderNameLike(StatementStatus.valueOf(status), "%" + creatorNamePart + "%", pageRequest);
    }

    public Statement changeStatus(Long statementId, StatementStatus newStatus) {
        Statement statement = statementRepository.findStatementById(statementId);
        // if (newStatus.ordinal() < statement.getStatus().ordinal()) {
        //     throw new UpdateStatementError();
        // }
        statement.setStatus(newStatus);
        
        if (newStatus.equals(StatementStatus.ACCEPTED) || newStatus.equals(StatementStatus.REJECTED)) {
            statement.setClosed(LocalDateTime.now());
        }
        statementRepository.save(statement);
        log.info("statement " + statement.getId() + " changed to " + newStatus.name());
        return statement;
    }

    public ArrayList<Statement> getNotDraftStatementsByUserId(Long userId, Integer offset, Integer limit, Sort.Direction sortDirection) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(sortDirection, "created"));
        return (ArrayList<Statement>) statementRepository.findStatementBySenderIdAndStatusNot(userId, StatementStatus.DRAFT, pageRequest);
    }

    public ArrayList<Statement> getSendStatementsByUserId(Long userId, Integer offset, Integer limit, Sort.Direction sortDirection) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by(sortDirection, "created"));
        return (ArrayList<Statement>) statementRepository.findStatementBySenderIdAndStatus(userId, StatementStatus.SEND, pageRequest);
    }

}
