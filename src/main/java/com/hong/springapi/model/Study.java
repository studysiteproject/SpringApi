package com.hong.springapi.model;

import com.hong.springapi.dto.StudyRequestDto;
import lombok.*;

import javax.persistence.*;

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
    private int maxman;

    @Column
    @Builder.Default
    private int nowman = 1;

    @Column
    private String category;

    @Column
    private String description;

    @Column
    private String place;

    @Column
    @Builder.Default
    private Boolean isactive = true;

    @Builder.Default
    @Column
    private int warn_cnt = 0;

    public void update(StudyRequestDto requestDto){
        this.title = requestDto.getTitle();
        this.description = requestDto.getDescription();
        this.maxman = requestDto.getMaxman();
        this.place = requestDto.getPlace();
    }

    public void updateNowman(int val) {
        this.nowman += val;
    }
    public void updateIsactive() {
        this.isactive = !this.isactive;
    }

}
