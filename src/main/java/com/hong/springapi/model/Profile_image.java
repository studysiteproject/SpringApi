package com.hong.springapi.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "profile_image")
public class Profile_image {
    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User userId;

    String img_url;
}
