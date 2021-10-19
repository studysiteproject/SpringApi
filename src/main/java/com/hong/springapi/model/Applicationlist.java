package com.hong.springapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(ApplicationlistKey.class)
public class Applicationlist extends Timestamped {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column (name = "study_id")
    private Long studyId;

    @Column
    private Boolean permission;

    @Column
    private String description;

    public void update(Boolean permission) {
        this.permission = permission;
    }
}
