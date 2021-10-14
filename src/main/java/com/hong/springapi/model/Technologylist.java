package com.hong.springapi.model;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "technologylist")
public class Technologylist {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;


    private String name;
    private String category;

    private String icon_url;



}
