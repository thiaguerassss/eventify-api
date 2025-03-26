package com.thiago.eventify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CreateUserDTO(
        @NotBlank(message = "O nome do usuário não pode estar em branco.")
        @Size(max = 100, message = "O nome do usuário não pode ultrapassar 100 caracteres.")
        String name,

        @NotBlank(message = "CPF não pode estar em branco.")
        @CPF(message = "CPF inválido.")
        String cpf,

        @NotBlank(message = "E-mail não pode estar em branco.")
        @Email(message = "E-mail inválido.")
        String email,

        @NotBlank(message = "O PIN não pode estar em branco.")
        @Pattern(
                regexp = "^[0-9]{4}$",
                message = "O PIN deve conter exatamente 4 dígitos numéricos."
        )
        String pin
) {
}