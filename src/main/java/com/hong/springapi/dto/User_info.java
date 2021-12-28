package com.hong.springapi.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User_info {
    Long userId;
    String user_name;
    String img_url;
    public User_info(Long id, String name, String url){
        userId = id;
        user_name = name;
        img_url = url;
    }
}
