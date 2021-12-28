package com.hong.springapi.dto;

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
}
