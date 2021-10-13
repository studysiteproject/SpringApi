package com.hong.springapi.service;

import com.hong.springapi.dto.GetFavoriteDto;
import com.hong.springapi.dto.SearchRequestDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StudyService {
    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategorylistRepository categorylistRepository;
    private final UserRepository userRepository;
    private final User_favoriteRepository user_favoriteRepository;

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
         if(requestDto.getTech()!=null) {
             addCategory(studyRepository.findById(res.getId()).get(), requestDto.getTech());
         }
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

    @Transactional
    public Study addFavorite(GetFavoriteDto getFavoriteDto){
        Study study = studyRepository.findById(getFavoriteDto.getStudy_id()).get();
        User user = userRepository.findById(getFavoriteDto.getUser_id()).get();
        //user, study validity check

        user_favoriteRepository.save(User_favorite.builder()
                .study_id(study)
                .user_id(user)
                .build()
        );

        return study;
    }

    public List<Study> getFavoritelist(Long user_id){
        //user_id validity check


//        User user = userRepository.findByUser_idQuery(user_id).get();

        return user_favoriteRepository.findDistinctAllByUser_idQuery(user_id);
    }

    public List<Study> findbyparams(SearchRequestDto searchDto){
//parameter에 따라 스터디 목록 반환, 모두 null일 시 showall과 동일
//유효한 parameter인지 검증 필요한가?
        if(searchDto.getTech() == null ) {
            return studyRepository.findAllByTitleAndPlaceQuery(
                    searchDto.getTitle(), searchDto.getPlace());
        }
        else {
            return categorylistRepository.findDistinctAllByTitleAndPlaceAndTechQuery
                    (searchDto.getTitle(), searchDto.getPlace(),
                            searchDto.getTech());
        }

    }




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
