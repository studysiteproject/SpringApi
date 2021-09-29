package com.hong.springapi.controller;

import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudyController {

    private final StudyRepository studyRepository;

    // create
    @PostMapping("/study")
    public Study createStudy(@RequestBody StudyRequestDto requestDto){
        return studyRepository.save(Study.builder()
                // 장소가 공백이면 안됨
                .title(requestDto.getTitle())
                .user_id(requestDto.getUser_id())
                // maxman 2이상 이어야함
                .maxman(requestDto.getMaxman())
                .nowman(1)
                // 설명이 공백이면 안됨
                .description(requestDto.getDescription())
                // 장소가 공백이면 안됨
                .place(requestDto.getPlace())
                .warn_cnt(0)
                .build()
        );
    }

    // read all
    @GetMapping("/study")
    public List<Study> getStudys(){
        return studyRepository.findAll();
    }

    // read one
    @GetMapping("/study/{id}")
    public Study getStudy(@PathVariable Long id){
        return studyRepository.findById(id).orElseThrow(StudyNotFoundException::new);
    }

    // update
    @Transactional
    @PutMapping("/study/{id}")
    public Long updateStudy(@PathVariable Long id, @RequestBody StudyRequestDto requestDto){
        Study study = studyRepository.findById(id).orElseThrow(StudyNotFoundException::new);
        study.update(requestDto);
        return id;
    }

    // delete
    @DeleteMapping("/study/{id}")
    public Long deleteStudy(@PathVariable Long id) throws StudyNotFoundException {
        studyRepository.deleteById(id);
        return id;
    }
}
