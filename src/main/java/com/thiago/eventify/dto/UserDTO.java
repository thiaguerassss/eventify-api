package com.thiago.eventify.dto;

import java.util.UUID;

public record UserDTO(UUID id, String name, String cpf, String email, String pin) {
}
