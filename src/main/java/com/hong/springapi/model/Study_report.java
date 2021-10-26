package com.hong.springapi.model;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "study_report")
@IdClass(User_favoriteKey.class)
public class Study_report extends Timestamped {
    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user_id;

    @Id
    @ManyToOne
    @JoinColumn(name="study_id")
    private Study study_id;

    @Column
    private String description;
}
