package com.hong.springapi.dto;

import com.hong.springapi.model.Technologylist;
import com.hong.springapi.util.SQLdefend;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StudyDetailDto {
    Long id;
    String title;
    User_info user_info;
    String category;
    List<Technologylist> tech_info;
    String place;
    String description;
    Integer maxman;
    Integer nowman;
    Integer warn_cnt;
    LocalDateTime create_date;
    boolean isfavorite;
    boolean isactive;

//    public void setTitle(String input){
//        title = SQLdefend.checkSQL(input);
//    }
//    public void setCategory(String input){
//        category = SQLdefend.checkSQL(input);
//    }
//    public void setPlace(String input){
//        place = SQLdefend.checkSQL(input);
//    }
//    public void setDescription(String input){
//        description = SQLdefend.checkSQL(input);
//    }

    public void setUser_info(Long id, String user_name, String img_url){
        user_info.id = id;
        user_info.user_name = user_name;
        user_info.img_url = img_url;
    }
}
