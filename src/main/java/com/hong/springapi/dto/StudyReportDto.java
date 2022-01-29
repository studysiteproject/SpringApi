package com.hong.springapi.dto;


import com.hong.springapi.util.SQLdefend;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyReportDto {
    private String description;
//    public void setDescription(String input){
//        description = SQLdefend.checkSQL(input);
//    }
}
