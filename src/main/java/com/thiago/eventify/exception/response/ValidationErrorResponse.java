package com.thiago.eventify.exception.response;

import java.util.List;

public record ValidationErrorResponse(ErrorResponse errorInfo, List<String> validationErrors) {
}
