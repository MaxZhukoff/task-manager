package com.manager.handler;

import com.manager.model.response.ApiErrorResponse;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        var body = new ApiErrorResponse(
                "Invalid request parameters",
                String.valueOf(ex.getStatusCode().value()),
                ex.getClass().getSimpleName(),
                ex.getAllErrors().stream()
                        .map(MessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining("; ")));
        return handleExceptionInternal(
                ex, body, headers, BAD_REQUEST, request);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
                ex.getReason(),
                ex.getStatusCode().toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        ), ex.getStatusCode());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(PropertyReferenceException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
                ex.getPropertyName(),
                BAD_REQUEST.toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        ), BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        ApiErrorResponse body = new ApiErrorResponse(
                "Invalid request parameters",
                BAD_REQUEST.toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage());
        return this.handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
                "You need to log in or reauthorize",
                UNAUTHORIZED.toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        ), UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {
        return new ResponseEntity<>(new ApiErrorResponse(
                "Server error",
                INTERNAL_SERVER_ERROR.toString(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        ), INTERNAL_SERVER_ERROR);
    }
}
