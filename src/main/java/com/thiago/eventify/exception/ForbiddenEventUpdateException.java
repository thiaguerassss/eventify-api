package com.thiago.eventify.exception;

public class ForbiddenEventUpdateException extends RuntimeException {
    public ForbiddenEventUpdateException(String message) {
        super(message);
    }
}
