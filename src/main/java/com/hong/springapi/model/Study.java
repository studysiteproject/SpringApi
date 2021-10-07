package com.hong.springapi.model;

import com.hong.springapi.dto.StudyRequestDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "study")
public class Study extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name ="id", updatable = false, nullable = false)
    private Long id;

    @Column
    private String title;

    @Column
    private Long user_id;

    @Column
    private  int maxman;

    @Column
    private int nowman;

    @Column
    private String description;

    @Column
    private String place;

    @Column
    private int warn_cnt;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "study_id")
//    private List<Categorylist> categorylists;



    public void update(StudyRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.maxman = requestDto.getMaxman();
        this.place = requestDto.getPlace();
    }
}
