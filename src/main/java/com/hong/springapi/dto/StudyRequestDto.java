package com.hong.springapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudyRequestDto {
    private String title;
    private Long user_id;
    private  int maxman;
    private String description;
    private String place;
}
