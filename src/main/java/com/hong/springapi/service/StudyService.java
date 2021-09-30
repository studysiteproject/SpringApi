package com.hong.springapi.service;

import com.hong.springapi.model.Study;
import com.hong.springapi.repository.StudyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
public class StudyService {
    private final StudyRepository studyRepository;
    @Autowired
    StudyService(StudyRepository studyRepository){
        this.studyRepository = studyRepository;
    }

    @Transactional
    public Optional<Study> join(Study study){
        validateDuplicationStudy(study.getId());
        studyRepository.save(study);
        return studyRepository.findById(study.getId());
    }

    private void validateDuplicationStudy(Long id){
        studyRepository.findById(id)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 스터디입니다.");
                });
    }

    private void validateUser(Long id){

    }

}
