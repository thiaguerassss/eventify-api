package com.thiago.eventify.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_events")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    @NotBlank(message = "O ID do dono do evento deve ser informado.")
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
    private LocalDateTime dateTime;

    @Column(name = "cep", nullable = false)
    @Pattern(
            regexp = "^\\d{5}-?\\d{3}$",
            message = "CEP inválido. Use o formato 12345-678 ou 12345678."
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

    @Column(name = "country", nullable = false)
    @NotBlank(message = "O país do evento não pode ser vazio.")
    private String country;
}
