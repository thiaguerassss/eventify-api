package com.thiago.eventify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateEventDTO(
        @NotNull(message = "O ID do dono do evento deve ser informado.")
        UUID ownerId,

        @NotBlank(message = "O título do evento não pode ser vazio.")
        @Size(max = 100, message = "O título deve conter no máximo 100 caracteres.")
        String title,

        @NotBlank(message = "A descrição do evento não pode ser vazia.")
        String description,

        @NotNull(message = "A data do evento não pode ser nula.")
        @Future(message = "A data do evento deve ser futura.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateTime,

        @NotBlank(message = "O CEP não pode ser vazio.")
        @Pattern(
                regexp = "^[0-9]{5}-[0-9]{3}$",
                message = "CEP inválido. Use o formato 12345-678."
        )
        String cep,

        String addressNumber
) {
}
