package com.thiago.eventify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "O nome do usuário não pode estar em branco.")
    @Size(max = 100, message = "O nome do usuário não pode ultrapassar 100 caracteres.")
    private String name;

    @Column(name = "cpf", unique = true, nullable = false)
    @CPF(message = "CPF inválido.")
    private String cpf;

    @Column(name = "email", nullable = false)
    @Email(message = "E-mail inválido.")
    private String email;

    @Column(name = "pin", nullable = false, length = 4)
    @Pattern(
            regexp = "^[0-9]{4}$",
            message = "O PIN deve conter exatamente 4 dígitos numéricos."
    )
    private String pin;

    @ManyToMany(mappedBy = "participants")
    private Set<Event> participatingEvents = new HashSet<>();

    public User(UUID id, String name, String cpf, String email, String pin) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.pin = pin;
    }
}
