package com.hong.springapi.dto;

import lombok.Getter;
import lombok.Setter;

public class StudyRequestDto {
    private String title;
    private Long user_id;
    private  int maxman;
    private String description;
    private String place;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public int getMaxman() {
        return maxman;
    }

    public void setMaxman(int maxman) {
        this.maxman = maxman;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
}
