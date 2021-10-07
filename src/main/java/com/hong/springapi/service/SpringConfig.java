package com.hong.springapi.service;

import com.hong.springapi.repository.CategorylistRepository;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.repository.TechnologylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class SpringConfig {
    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategorylistRepository categorylistRepository;


    @Bean
    public StudyService studyService(){
        return new StudyService(this.studyRepository, this.technologylistRepository, this.categorylistRepository);
    }
}
