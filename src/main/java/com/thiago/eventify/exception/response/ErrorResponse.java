package com.thiago.eventify.exception.response;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String error, String message, String path, LocalDateTime timestamp) {
}
