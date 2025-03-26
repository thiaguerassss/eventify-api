package com.thiago.eventify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserDTO(
        @Size(max = 100, message = "O nome do usuário não pode ultrapassar 100 caracteres.")
        String name,

        @Email(message = "E-mail inválido.")
        String email,

        @Pattern(
                regexp = "^[0-9]{4}$",
                message = "O PIN deve conter exatamente 4 dígitos numéricos."
        )
        String pin
) {
}
