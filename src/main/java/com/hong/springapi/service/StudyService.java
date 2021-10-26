package com.hong.springapi.service;

import com.hong.springapi.dto.GetFavoriteDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.BadRequestException;
import com.hong.springapi.exception.exceptions.TokenValidationException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import com.hong.springapi.response.Response;
import com.hong.springapi.util.CookieHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.hong.springapi.dto.ApplicationlistDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.StudyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategorylistRepository categorylistRepository;
    private final UserRepository userRepository;
    private final User_favoriteRepository user_favoriteRepository;
    private final ApplicationlistRepository applicationlistRepository;

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

    @Transactional
    public ResponseEntity<Response> updateStudy(Long studyId, StudyRequestDto requestDto, HttpServletRequest request) {
//        // 유효한 토큰인지 검사
//        if (!CookieHandler.checkValidation(request)){
//            throw new TokenValidationException();
//        }
        // 본인이 작성한 스터디글인지 검사
//        Long userId = CookieHandler.getUserIdFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
//        if (!study.getUserId().equals(userId)){
//            throw new UserValidationException();
//        }
        // 제목이 공백이면 안됨
        if (requestDto.getTitle().trim().equals("")) throw new BadRequestException();
        // 장소가 공백이면 안됨
        if (requestDto.getPlace().trim().equals("")) throw new BadRequestException();
        // maxman 2이상 이어야함
        if (requestDto.getMaxman() < 2) throw new BadRequestException();
        // 설명이 공백이면 안됨
        if (requestDto.getDescription().trim().equals("")) throw new BadRequestException();

        study.update(requestDto);
        // 카테고리 수정 종찬이랑 얘기
        if(requestDto.getTech() != null) {
            addCategory(study, requestDto.getTech());
        }
        return new ResponseEntity<>(new Response("success", "update success"), HttpStatus.OK);
    }

    public ResponseEntity<Response> deleteStudy(Long studyId, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long userId = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(userId)){
            throw new UserValidationException();
        }
        studyRepository.deleteById(studyId);

        return new ResponseEntity<>(new Response("success", "study delete success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> updateParticipationlist(Long studyId, List<ApplicationlistDto> requestDtolist, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long userId = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(userId)){
            throw new StudyNotFoundException();
        }

        List<Applicationlist> applicationlist = applicationlistRepository.findAllByStudy_id(studyId).orElseThrow(StudyNotFoundException::new);

        for (Applicationlist application : applicationlist){
            for (ApplicationlistDto requestDto : requestDtolist){
                if (requestDto.getUser_id().equals(application.getUser_id()))
                    application.update(requestDto.getPermission());
            }
        }
        return new ResponseEntity<> (new Response("success", "update success"), HttpStatus.OK);
    }


}
