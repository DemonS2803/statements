package ru.light.statements.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.light.statements.entities.Statement;

@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {
    
    List<Statement> findStatementBySenderId(Long senderId);
}
