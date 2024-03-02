package ru.light.statements.entities;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.light.statements.enums.UserRole;

@Entity
@Data
@Builder
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String password;
    private String firstName;
    private String lastName;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    @Transient
    private List<Statement> statements;
    private String phone;
    // по-хорошему в redis перенести
    private String refreshToken;
}
