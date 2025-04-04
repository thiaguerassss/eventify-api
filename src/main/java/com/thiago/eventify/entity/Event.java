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
    private UUID ownerId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "cep", nullable = false)
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

    @Column(name = "district", nullable = false)
    @NotBlank(message = "O bairro do evento não pode ser vazio.")
    private String district;

    @ManyToMany
    @JoinTable(
            name = "tb_event_participants",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<User> participants = new HashSet<>();

    public Event(UUID id, UUID ownerId, String title, String description, LocalDateTime dateTime, String cep,
                 String address, String addressNumber, String city, String state, String district) {
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
        this.district = district;
    }
}
