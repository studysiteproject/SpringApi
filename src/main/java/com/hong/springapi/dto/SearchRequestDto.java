package com.hong.springapi.dto;

import com.hong.springapi.util.SQLdefend;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SearchRequestDto {
    private String title;
    private String place;
    private String category;
    private List<String> tech;
//    public void setTitle(String input){
//        title = SQLdefend.checkSQL(input);
//    }
//    public void setPlace(String input){
//        place = SQLdefend.checkSQL(input);
//    }
//    public void setCategory(String input){
//        category = SQLdefend.checkSQL(category);
//    }
//    public void setTech(List<String> inputs){
//        tech.clear();
//        tech.addAll(SQLdefend.checkSQL(inputs));
//    }

}
