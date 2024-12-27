package com.bmri.blogbackend.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ControllerAdvice
public class GlobalExceptionHandler {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        var error = new ValidationErrorResponse();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            error.getViolations().add(
                    new Violation(formatter.format(LocalDateTime.now()), HttpStatus.BAD_REQUEST.value(),
                            violation.getClass().getName(), violation.getRootBeanClass().getName(),
                            violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage())
            );
        }
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var error = new ValidationErrorResponse();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getViolations().add(
                    new Violation(formatter.format(LocalDateTime.now()), HttpStatus.BAD_REQUEST.value(),
                            fieldError.getClass().getName(), fieldError.getObjectName(),
                            fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage())
            );
        }
        return error;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    StandardErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return new StandardErrorResponse(formatter.format(LocalDateTime.now()), HttpStatus.BAD_REQUEST.value(),
                e.getClass().getName(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    StandardErrorResponse handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new StandardErrorResponse(formatter.format(LocalDateTime.now()), HttpStatus.CONFLICT.value(),
                e.getClass().getName(), e.getMessage());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    StandardErrorResponse handleObjectNotFoundException(ObjectNotFoundException e) {
        return new StandardErrorResponse(formatter.format(LocalDateTime.now()), HttpStatus.NOT_FOUND.value(),
                e.getClass().getName(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    StandardErrorResponse handleGeneralException(Exception e) {
        return new StandardErrorResponse(formatter.format(LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getClass().getName(), e.getMessage());
    }

}
