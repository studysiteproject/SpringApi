package com.hong.springapi.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategorylistKey implements Serializable {
    private Long study_id;
    private Long tech_id;

    public CategorylistKey() { }
    public CategorylistKey(Long study_id, Long tech_id){
        this.study_id = study_id;
        this.tech_id = tech_id;
    }


}
