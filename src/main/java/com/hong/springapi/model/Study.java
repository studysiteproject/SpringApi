package com.hong.springapi.model;

import com.hong.springapi.dto.StudyRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Study extends Timestamped {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;

    @Column
    private String title;

    @Column (name = "user_id")
    private Long userId;

    @Column
    private  int maxman;

    @Column
    @Builder.Default
    private int nowman = 1;

    @Column
    private String description;

    @Column
    private String place;

    @Builder.Default
    @Column(name = "warn_cnt")
    private int warnCnt = 0;

    public void update(StudyRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.maxman = requestDto.getMaxman();
        this.place = requestDto.getPlace();
    }
}
