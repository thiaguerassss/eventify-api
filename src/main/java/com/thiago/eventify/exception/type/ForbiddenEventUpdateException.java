package com.thiago.eventify.exception.type;

public class ForbiddenEventUpdateException extends RuntimeException {
    public ForbiddenEventUpdateException(String message) {
        super(message);
    }
}
