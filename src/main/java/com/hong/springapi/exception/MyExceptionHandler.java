package com.hong.springapi.exception;

import com.hong.springapi.exception.exceptions.BadRequestException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.response.Response;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.TokenValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(StudyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response reportError(StudyNotFoundException exception) {
        return new Response("fail", exception.getMessage());
    }

    @ExceptionHandler(TokenValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response reportError(TokenValidationException exception) {
        return new Response("fail", exception.getMessage());
    }

    @ExceptionHandler(UserValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response reportError(UserValidationException exception) {
        return new Response("fail", exception.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response reportError(BadRequestException exception) {
        return new Response("fail", exception.getMessage());
    }
}
