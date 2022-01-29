package com.hong.springapi.dto;

import com.hong.springapi.util.SQLdefend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StudyRequestDto {
    private String title;
    private int maxman;
    private String description;
    private String place;
    private String category;
    private List<String> tech;
//    public void setTitle(String input){
//        title = SQLdefend.checkSQL(input);
//    }
//    public void setDescription(String input){
//        description = SQLdefend.checkSQL(input);
//    }
//    public void setPlace(String input){
//        place = SQLdefend.checkSQL(input);
//    }
//    public void setCategory(String input){
//        category = SQLdefend.checkSQL(input);
//    }
//    public void setTech(List<String> input){
//        tech.clear();
//        tech.addAll(SQLdefend.checkSQL(input));
//    }
}
