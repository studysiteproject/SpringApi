package com.hong.springapi.service;

import com.hong.springapi.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.swing.*;

@Configuration
public class SpringConfig {
    private final StudyRepository studyRepository;

    @Autowired
    SpringConfig(StudyRepository studyRepository){
        this.studyRepository  = studyRepository;
    }

    @Bean
    public StudyService studyService(){
        return new StudyService(this.studyRepository);
    }
}
