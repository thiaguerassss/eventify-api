package com.thiago.eventify.exception.type;

public class ForbiddenRegisterException extends RuntimeException {
    public ForbiddenRegisterException(String message) {
        super(message);
    }
}
