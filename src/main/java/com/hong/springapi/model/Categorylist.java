package com.hong.springapi.model;


import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@IdClass(CategorylistKey.class)
public class Categorylist {

    @Id
    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study_id;

    @Id
    @ManyToOne
    @JoinColumn(name = "tech_id")
    private Technologylist tech_id;

}

