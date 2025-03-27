package com.thiago.eventify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "tb_events")
@Data
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    @NotNull(message = "O ID do dono do evento deve ser informado.")
    private UUID ownerId;

    @Column(name = "title", nullable = false, length = 100)
    @NotBlank(message = "O título do evento não pode ser vazio.")
    @Size(max = 100, message = "O título deve conter no máximo 100 caracteres.")
    private String title;

    @Column(name = "description", nullable = false)
    @NotBlank(message = "A descrição do evento não pode ser vazia.")
    private String description;

    @Column(name = "date_time", nullable = false)
    @Future(message = "A data do evento tem que ser uma data futura.")
    @NotNull(message = "A data do evento não pode ser nula.")
    private LocalDateTime dateTime;

    @Column(name = "cep", nullable = false)
    @NotBlank(message = "O CEP não pode ser vazio.")
    @Pattern(
            regexp = "^[0-9]{5}-[0-9]{3}$",
            message = "CEP inválido. Use o formato 12345-678."
    )
    private String cep;

    @Column(name = "address", nullable = false)
    @NotBlank(message = "O endereço do evento não pode ser vazio.")
    private String address;

    @Column(name = "address_number")
    private String addressNumber;

    @Column(name = "city", nullable = false)
    @NotBlank(message = "A cidade do evento não pode ser vazia.")
    private String city;

    @Column(name = "state", nullable = false)
    @NotBlank(message = "O estado do evento não pode ser vazio.")
    private String state;

    @ManyToMany
    @JoinTable(
            name = "tb_event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<User> participants = new HashSet<>();

    public Event(UUID id, UUID ownerId, String title, String description, LocalDateTime dateTime, String cep,
                 String address, String addressNumber, String city, String state) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.cep = cep;
        this.address = address;
        this.addressNumber = addressNumber;
        this.city = city;
        this.state = state;
    }
}
