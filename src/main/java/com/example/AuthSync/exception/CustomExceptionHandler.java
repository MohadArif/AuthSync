package com.example.AuthSync.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Log4j2
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> validationExceptionHandler(
            MethodArgumentNotValidException ex){
        Map<String ,String> errors=new HashMap<>();

        log.warn("Validation error occurred: {}",ex.getMessage());
        ex.getBindingResult().getFieldErrors().forEach(
                error->{log.info("Field : {} Error: {}",error.getField(),error.getDefaultMessage());
                    errors.put(error.getField(),error.getDefaultMessage());});
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String,String>> handleEmailAlreadyExistsException(EmailAlreadyExistException ex){
        Map<String,String> errors=new HashMap<>();
        log.warn("Email already exist {}",ex.getMessage());
        errors.put("message","email already exists");
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }
}
