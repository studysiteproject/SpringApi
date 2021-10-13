package com.hong.springapi.exception;

import com.hong.springapi.Response.ErrorResponse;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(StudyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse reportError(StudyNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse reportError(UserValidationException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
    }
}
