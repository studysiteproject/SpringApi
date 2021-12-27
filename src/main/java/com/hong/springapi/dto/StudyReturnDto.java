package com.hong.springapi.dto;

import com.hong.springapi.model.Technologylist;
import com.hong.springapi.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StudyReturnDto {
    Long id;
    String title;
    User_info user_info;
    String category;
    List<Technologylist> tech_info;
    String place;
    Integer maxman;
    Integer nowman;
    Integer warn_cnt;
    LocalDateTime create_date;
    boolean isfavorite;
    public StudyReturnDto(){
        isfavorite = false;
        tech_info = new ArrayList<Technologylist>();
       // user_info = new User_info();
    }
    public void setUser_info(Long id, String user_name, String img_url){
        user_info.id = id;
        user_info.user_name = user_name;
        user_info.img_url = img_url;
    }

}

//@Getter
//@Setter
//class Tech_info{
//    Long id;
//    String tech_name;
//    String img_url;
//}
    /*
      "id": 1,
    "title": "스터디 구인",
    "user_info": {
        "id": 3,
        "user_name": "테스트이름",
        "img_url": "https://catchstudy-images.s3.ap-northeast-2.amazonaws.com/profile/default.png"
    },
		"category" : "develop",
    "tech_info": [
        {
            "id": 1,
            "tech_name": "Spring",
            "img_url": "img/icon/tech/spring.svg"
        },
        {
            "id": 2,
            "tech_name": "Github",
            "img_url": "img/icon/tech/github.svg"
        }
    ],
		"place" : "online",
    "maxman": 3,
    "nowman": 1,
    "warn_cnt": 0,
    "create_date": "2021-10-13T19:02:48.252",
		"isfavorite" : false
     */