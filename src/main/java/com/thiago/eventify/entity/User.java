package com.thiago.eventify.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String name;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "pin", nullable = false, length = 4)
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
