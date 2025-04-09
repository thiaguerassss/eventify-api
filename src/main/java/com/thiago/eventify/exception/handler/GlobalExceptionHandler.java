package com.thiago.eventify.exception.handler;

import com.thiago.eventify.exception.response.ErrorResponse;
import com.thiago.eventify.exception.response.ValidationErrorResponse;
import com.thiago.eventify.exception.type.*;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.ObjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest req){
        ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), message, req.getRequestURI(),
                LocalDateTime.now());
        logger.error("Exceção: {} - {}", message, req.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ObjectNotFoundException ex, HttpServletRequest req){
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            ForbiddenEventUpdateException.class,
            ForbiddenRegisterException.class,
            ImpossibleUnregisterException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException ex, HttpServletRequest req) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException ex, HttpServletRequest req) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignError(FeignException ex, HttpServletRequest req) {
        String message = "Erro ao acessar serviço externo: " + ex.getMessage();
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, message, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationError(MethodArgumentNotValidException ex,
                                                                         HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse error = new ErrorResponse(status.value(), status.getReasonPhrase(), "Erro de validação.",
                req.getRequestURI(), LocalDateTime.now());
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(
                f -> f.getField() + ": " + f.getDefaultMessage()).toList();
        ValidationErrorResponse validationError = new ValidationErrorResponse(error, errors);
        logger.warn("Erro de validação: {}", errors);
        return ResponseEntity.status(status).body(validationError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        logger.error("Erro interno inesperado: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.", req);
    }
}