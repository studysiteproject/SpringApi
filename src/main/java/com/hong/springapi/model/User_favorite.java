package com.hong.springapi.model;


import lombok.*;

import javax.persistence.*;
import java.sql.Time;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user_favorite")
@IdClass(User_favoriteKey.class)
public class User_favorite extends Timestamped {

    @Id
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user_id;

    @Id
    @ManyToOne
    @JoinColumn(name="study_id")
    private Study study_id;


}
