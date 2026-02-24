package com.api.blog.ErrorHandling;

import com.api.blog.ErrorHandling.customExceptions.ResourceNotFoundException;
import com.api.blog.ErrorHandling.errorsDto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));

    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(ResourceNotFoundException ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("ResourceNotFound", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse>handleAccessDeniedException(AccessDeniedException ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("AccessDenied", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors));

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("IllegalArgument", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors));

    }
}
