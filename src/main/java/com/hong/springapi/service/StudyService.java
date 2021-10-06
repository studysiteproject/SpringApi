package com.hong.springapi.service;

import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.model.Categorylist;
import com.hong.springapi.model.CategorylistKey;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.CategoryListRepository;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.repository.TechnologylistRepository;
import lombok.RequiredArgsConstructor;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Transactional
public class StudyService {
    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategoryListRepository categorylistRepository;


    @Transactional
    public Study join(StudyRequestDto requestDto){

         Study res =studyRepository.save(Study.builder()
                .title(requestDto.getTitle())
                .user_id(requestDto.getUser_id())
                .place(requestDto.getPlace())
                .maxman(requestDto.getMaxman())
                .warn_cnt(0)
                .nowman(1)
                .description(requestDto.getDescription())
                .build()
        );
        addCategory(studyRepository.findById(res.getId()).get(), requestDto.getTech());

        return res;

    }

    @Transactional
    public void addCategory(Study study, List<String> tech){
        for(int i=0; i<tech.size(); i++){
            categorylistRepository.save(Categorylist.builder()
                    .study_id(study)
                    .tech_id(technologylistRepository.findByName(tech.get(i)).get())
                    .build()
            );
        }
    }


    public List<Study> showall(){
        return studyRepository.findAll();
    }

//    public List<Study> findbyparams(SearchRequestDto searchDto){
//
//    }




    //나중에 구현
    private void validateDuplicationStudy(Long id){
        studyRepository.findById(id)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 스터디입니다.");
                });
    }

    private void validateUser(Long id){

    }

}
