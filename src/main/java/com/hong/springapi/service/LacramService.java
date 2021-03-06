package com.hong.springapi.service;

import com.hong.springapi.dto.GetFavoriteDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.*;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import com.hong.springapi.response.Response;
import com.hong.springapi.util.CookieHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.hong.springapi.dto.ApplicationlistDto;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.StudyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LacramService {

    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategorylistRepository categorylistRepository;
    private final UserRepository userRepository;
    private final User_favoriteRepository user_favoriteRepository;
    private final ApplicationlistRepository applicationlistRepository;

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
    public ResponseEntity<Response> updateStudy(Long study_id, StudyRequestDto requestDto, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)){
            throw new UserValidationException();
        }

        // 제목이 공백이면 안됨
        if (requestDto.getTitle().trim().equals("")) throw new BadRequestException();
        // 장소가 공백이면 안됨
        if (requestDto.getPlace().trim().equals("")) throw new BadRequestException();
        // maxman 2이상 이어야함
        if (requestDto.getMaxman() < 2 || requestDto.getMaxman() > 50) throw new BadRequestException();
        // 설명이 공백이면 안됨
        if (requestDto.getDescription().trim().equals("")) throw new BadRequestException();
        // 카테고리가 기준에서 벗어나면 안됨


        study.update(requestDto);

        // 기술목록 수정
        categorylistRepository.deleteAllByStudyIdQuery(study_id);
        if(requestDto.getTech() != null) {
            addCategory(study, requestDto.getTech());
        }
        return new ResponseEntity<>(new Response("success", "update success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> deleteStudy(Long study_id, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)){
            throw new UserValidationException();
        }
        studyRepository.deleteById(study_id);

        return new ResponseEntity<>(new Response("success", "study delete success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> updateParticipationlist(Long study_id, List<ApplicationlistDto> requestDtolist, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)){
            throw new StudyNotFoundException();
        }

        List<Applicationlist> applicationlist = applicationlistRepository.findAllByStudy_id(study_id).orElseThrow(StudyNotFoundException::new);

        for (Applicationlist application : applicationlist){
            for (ApplicationlistDto requestDto : requestDtolist){
                if (requestDto.getUser_id().equals(application.getUser_id())){
                    // 신청후 허용이 되었을 경우 nowman++
                    if (!application.getPermission() && requestDto.getPermission())
                        updateNowman(study_id, 1);
                    // 스터디 유저를 추방할 경우 nowman--
                    if (application.getPermission() && !requestDto.getPermission())
                        updateNowman(study_id, -1);
                    application.update(requestDto.getPermission());
                }
            }
        }
        return new ResponseEntity<> (new Response("success", "update success"), HttpStatus.OK);
    }

    @Transactional
    public void updateNowman(Long id, int val){
        Study study = studyRepository.findById(id).orElseThrow(StudyNotFoundException::new);
        study.updateNowman(val);
    }

    @Transactional
    public ResponseEntity<Response> updateParticipation(Long study_id, Long app_user_id, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)){
            throw new UserValidationException();
        }

        Applicationlist applicationlist = applicationlistRepository.findByUser_idAndStudy_id(app_user_id, study_id)
                .orElseThrow(UserNotFoundException::new);

        Boolean permission = applicationlist.getPermission();

        // 이미 스터디원이 다 찼는데 허용하려는 경우
        if (study.getNowman() == study.getMaxman() && !permission){
            throw new BadRequestException();
        }

        // 허용 -> 비허용 nowman-- , 비허용 -> 허용 nowman++
        if (permission) {
            updateNowman(study_id, -1);
        } else {
            updateNowman(study_id, 1);
        }
        applicationlist.update(!permission);

        return new ResponseEntity<>(new Response("success", "participation update success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> deleteParticipation(Long study_id, Long app_user_id, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)){
            throw new UserValidationException();
        }

        Applicationlist applicationlist = applicationlistRepository.findByUser_idAndStudy_id(app_user_id, study_id)
                .orElseThrow(UserNotFoundException::new);

        Boolean permission = applicationlist.getPermission();
        // 허용된 인원 삭제시 nowman--
        if (permission) {
            updateNowman(study_id, -1);
        }
        applicationlistRepository.deleteApplicationlist(app_user_id, study_id);

        return new ResponseEntity<>(new Response("success", "participation delete success"), HttpStatus.OK);
    }

    // 모집여부 수정
    @Transactional
    public ResponseEntity<Response> updateRecruit(Long study_id, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)){
            throw new StudyNotFoundException();
        }

        study.updateIsactive();

        return new ResponseEntity<>(new Response("success", "isactive update success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> deleteApplicationlist(Long study_id, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 신청한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        List<Applicationlist> myApplicationlist = applicationlistRepository.findAllByUser_id(user_id).orElseThrow(StudyNotFoundException::new);

        for (Applicationlist myApplication : myApplicationlist){
            if (myApplication.getStudy_id().equals(study_id)){
                applicationlistRepository.delete(myApplication);
                // 탈퇴시 nowman--
                Boolean permission = myApplication.getPermission();
                // 허용된 인원 탈퇴시 nowman--
                if (permission) {
                    updateNowman(study_id, -1);
                }

                return new ResponseEntity<> (new Response("success", "delete success"), HttpStatus.OK);
            }
        }
        throw new StudyNotFoundException();
    }
}
