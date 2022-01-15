package com.hong.springapi.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "profile_image")
public class Profile_image{

    @Id
    @Column(name = "user_id")
    private Long id;

    @MapsId
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String img_url;
}
