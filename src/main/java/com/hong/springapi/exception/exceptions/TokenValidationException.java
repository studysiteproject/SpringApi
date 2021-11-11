package com.hong.springapi.exception.exceptions;

public class TokenValidationException extends RuntimeException{
    public TokenValidationException(){
        super("로그인 후 이용해주세요.");
    }
}
