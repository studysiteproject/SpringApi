package com.hong.springapi.exception.exceptions;

public class StudyNotFoundException extends RuntimeException{
    public StudyNotFoundException(){
        super("존재하지 않는 게시글입니다.");
    }
}
