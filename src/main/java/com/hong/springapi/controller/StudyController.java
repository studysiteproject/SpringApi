package com.hong.springapi.controller;

import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class StudyController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;

    // create
    @PostMapping("/study")
    public Study createStudy(@RequestBody StudyRequestDto requestDto){
        return studyService.createStudy(requestDto);
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
    @PutMapping("/study/{id}")
    public Long updateStudy(@PathVariable Long id, @RequestBody StudyRequestDto requestDto){
        return studyService.update(id,requestDto);
    }

    // delete
    @DeleteMapping("/study/{id}")
    public Long deleteStudy(@PathVariable Long id) throws StudyNotFoundException {
        studyRepository.deleteById(id);
        return id;
    }
}
