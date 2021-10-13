package com.hong.springapi.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name ="id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private String user_id;

    @Column
    private String user_pw;
    @Column
    private String user_name;
    @Column
    private String user_email;
    @Column
    private String user_job;
    @Column
    private int warning_cnt;
    @Column
    private String account_state;

    //어차피 여기서 유저 생성할 일 없을 것 같아서 timestamped랑 연동은 필요없을듯?
    @Column
    private LocalDateTime create_date;
//    @Column
//    private String user_comment;
}
