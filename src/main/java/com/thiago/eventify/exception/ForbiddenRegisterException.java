package com.thiago.eventify.exception;

public class ForbiddenRegisterException extends RuntimeException {
    public ForbiddenRegisterException(String message) {
        super(message);
    }
}
