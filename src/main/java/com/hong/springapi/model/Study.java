package com.hong.springapi.model;

import com.hong.springapi.dto.StudyRequestDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "study")
public class Study extends Timestamped {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column
    private String title;

    @Column(name = "user_id")
    private Long userId;

    @Column
    private  int maxman;

    @Column
    private int nowman;

    @Column
    private String category;

    @Column
    private String description;

    @Column
    private String place;

    @Column
    private int warn_cnt;

    public void update(StudyRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.maxman = requestDto.getMaxman();
        this.place = requestDto.getPlace();
    }
}
