package com.hong.springapi.service;

import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;

    @Transactional
    public Long update(Long id, StudyRequestDto requestDto) {
        Study study = studyRepository.findById(id).orElseThrow(StudyNotFoundException::new);
        study.update(requestDto);
        return id;
    }

    public Study createStudy(StudyRequestDto requestDto) {
        return studyRepository.save(Study.builder()
                // 장소가 공백이면 안됨
                .title(requestDto.getTitle())
                .user_id(requestDto.getUser_id())
                // maxman 2이상 이어야함
                .maxman(requestDto.getMaxman())
                // 설명이 공백이면 안됨
                .description(requestDto.getDescription())
                // 장소가 공백이면 안됨
                .place(requestDto.getPlace())
                .build()
        );
    }
}
