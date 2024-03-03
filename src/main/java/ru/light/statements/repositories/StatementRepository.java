package ru.light.statements.repositories;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.light.statements.entities.Statement;
import ru.light.statements.enums.StatementStatus;

@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {
    
    Statement findStatementById(Long id);
    List<Statement> findStatementBySenderId(Long senderId);
    List<Statement> findStatementBySenderId(Long senderId, PageRequest pageRequest);
    List<Statement> findStatementBySenderIdAndStatus(Long senderId, StatementStatus status, PageRequest pageRequest);
    List<Statement> findStatementByStatus(StatementStatus status, PageRequest pageRequest);
    List<Statement> findStatementBySenderNameLike(String senderName, PageRequest pageRequest);
    List<Statement> findStatementByStatusAndSenderNameLike(StatementStatus status, String senderNamePart, PageRequest pageRequest);
}
