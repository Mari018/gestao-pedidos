package com.gestao.pedidos.exeption.errorhandling;

import com.gestao.pedidos.exeption.OrderNotFoundException;
import com.gestao.pedidos.exeption.UserNotFoundException;
import com.gestao.pedidos.model.ErrorLog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionHandler {
    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            OrderNotFoundException.class,
            UserNotFoundException.class,
            RuntimeException.class
    }
    )
    public ResponseEntity<Error> handleNotFound(Exception e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.builder()
                .timeStamp(new Date())
                .message(e.getMessage())
                .method(request.getMethod())
                .requestURI(request.getRequestURI())
                .build());


    }


    @org.springframework.web.bind.annotation.ExceptionHandler(value = {
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Error> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Error.builder()
                .timeStamp(new Date())
                .message("validations errors:" + errors)
                .method(request.getMethod())
                .requestURI(request.getRequestURI())
                .build());
    }
}
