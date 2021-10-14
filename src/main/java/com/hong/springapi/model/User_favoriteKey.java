package com.hong.springapi.model;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class User_favoriteKey implements Serializable {

    private Long user_id;
    private Long study_id;

    public User_favoriteKey(){}
    public User_favoriteKey(Long user_id, Long study_id){
        this.user_id  = user_id;
        this.study_id = study_id;
    }
}
