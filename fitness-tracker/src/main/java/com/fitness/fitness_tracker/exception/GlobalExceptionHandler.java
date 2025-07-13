package com.fitness.fitness_tracker.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JwtException.class)
    public ProblemDetail handleJwtExceptions(JwtException exception) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        String title = "Invalid Token";
        String description = "The provided token is invalid, expired, or malformed.";

        if (exception instanceof ExpiredJwtException) {
            description = "The provided authentication token has expired. Please log in again.";
            title = "Token Expired";
        } else if (exception instanceof SignatureException) {
            description = "The provided token signature is invalid.";
            title = "Invalid Signature";
        }

        ProblemDetail errorDetail = ProblemDetail.forStatus(status);
        errorDetail.setTitle(title);
        errorDetail.setDetail(description);

        return errorDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
        ProblemDetail errorDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        errorDetail.setTitle("Access Denied");
        errorDetail.setDetail("You are not authorized to access this resource.");
        return errorDetail;
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }
}
