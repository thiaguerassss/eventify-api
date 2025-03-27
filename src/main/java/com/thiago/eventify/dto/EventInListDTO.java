package com.thiago.eventify.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventInListDTO(UUID id, String title, String description, LocalDateTime dateTime, String cep,
                             String address, String addressNumber, String city, String state) {
}
