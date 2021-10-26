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
@Entity(name = "applicationlist")
@IdClass(ApplicationlistKey.class)
public class Applicationlist extends Timestamped {

    @Id
    @Column()
    private Long user_id;

    @Id
    @Column()
    private Long study_id;

    @Column
    private Boolean permission;

    @Column
    private String description;

    public void update(Boolean permission) {
        this.permission = permission;
    }
}
