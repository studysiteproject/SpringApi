package com.hong.springapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetApplicationDto {
    private User_info user_info;
    private Boolean permission;
    public GetApplicationDto(){
        user_info = new User_info();
    }
}
