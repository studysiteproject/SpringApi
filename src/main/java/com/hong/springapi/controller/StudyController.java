package com.hong.springapi.controller;

import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class StudyController {

    private final StudyService studyService;

    // create
    @Transactional
    @PostMapping("/study")
    public Optional<Study> createStudy(@RequestBody StudyRequestDto requestDto){
       Study study = new Study();
       study.setTitle(requestDto.getTitle());
       study.setUser_id(requestDto.getUser_id());
       study.setPlace(requestDto.getPlace());
       study.setMaxman(requestDto.getMaxman());
       study.setWarn_cnt(0);
       study.setNowman(1);
       study.setDescription(requestDto.getDescription());

       return studyService.join(study);
    }
/*
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
*/
}

