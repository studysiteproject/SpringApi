package com.hong.springapi.model;

import com.hong.springapi.dto.StudyRequestDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
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

    public void update(StudyRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.maxman = requestDto.getMaxman();
        this.place = requestDto.getPlace();
    }
}
