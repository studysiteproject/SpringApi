package com.hong.springapi.dto;

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
}
