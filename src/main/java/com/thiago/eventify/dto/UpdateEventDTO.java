package com.thiago.eventify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateEventDTO(
        @Size(max = 100, message = "O título deve conter no máximo 100 caracteres.")
        String title,

        String description,

        @Future(message = "A data do evento deve ser futura.")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dateTime,

        @Pattern(
                regexp = "^[0-9]{5}-[0-9]{3}$",
                message = "CEP inválido. Use o formato 12345-678."
        )
        String cep,

        String addressNumber
) {
}
