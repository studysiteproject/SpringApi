package com.hong.springapi.exception.exceptions;

public class UserValidationException extends RuntimeException{
    public UserValidationException(){
        super("권한이 없습니다.");
    }
}
