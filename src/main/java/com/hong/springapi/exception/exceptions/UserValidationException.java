package com.hong.springapi.exception.exceptions;

public class UserValidationException extends RuntimeException{
    public UserValidationException(){
        super("로그인 후 이용해주세요");
    }
}
