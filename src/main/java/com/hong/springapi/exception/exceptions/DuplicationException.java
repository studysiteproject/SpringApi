package com.hong.springapi.exception.exceptions;

public class DuplicationException extends RuntimeException{
    public DuplicationException(){
        super("이미 처리된 요청입니다.");
    }
}
