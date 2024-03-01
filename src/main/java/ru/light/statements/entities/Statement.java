package ru.light.statements.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.light.statements.enums.StatementStatus;

@Entity
@Data
@Builder
@Table(name = "statements")
@AllArgsConstructor
@NoArgsConstructor
public class Statement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private StatementStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;
    private LocalDateTime created;
    private LocalDateTime closed;
    // по тз не понял, нужно ли это поле. Вообще обошелся бы owner_id, но на всякий случай сделал;
    private String senderName;
    // хотел сделать отдельную таблицу, но показалось излишним
    private Integer countryCode;
    private Integer cityCode;
    private String phone;

}
