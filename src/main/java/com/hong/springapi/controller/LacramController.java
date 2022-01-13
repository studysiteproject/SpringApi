package com.hong.springapi.controller;

import com.hong.springapi.dto.ApplicationlistDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.dto.StudyReturnDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.TokenValidationException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.CategorylistRepository;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.repository.User_favoriteRepository;
import com.hong.springapi.response.Response;
import com.hong.springapi.service.LacramService;
import com.hong.springapi.service.StudyServicelimpet;
import com.hong.springapi.util.CookieHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class LacramController {

    private final StudyRepository studyRepository;
    private final LacramService studyService;
    private final StudyServicelimpet studyServicelimpet;
    private final ApplicationlistRepository applicationlistRepository;
    private final CategorylistRepository categorylistRepository;
    private final User_favoriteRepository user_favoriteRepository;

    // update
    @PutMapping("/study/{study_id}")
    public ResponseEntity<Response> updateStudy(@PathVariable Long study_id, @RequestBody StudyRequestDto requestDto, HttpServletRequest request){
        return studyService.updateStudy(study_id,requestDto,request);
    }

    // delete
    @DeleteMapping("/study/{study_id}")
    public ResponseEntity<Response> deleteStudy(@PathVariable Long study_id, HttpServletRequest request) throws StudyNotFoundException {
        return studyService.deleteStudy(study_id,request);
    }

    // ------생성한 스터디 관리--------

    // 생성한 스터디 전체 불러오기
    @GetMapping("/study/created")
    public List<StudyReturnDto> getCreatedStudy(HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        List<Study> studyList = studyRepository.findAllByUser_id(user_id).orElseThrow(UserValidationException::new);
        return studyServicelimpet.getformal(studyList, user_id);
    }

    // 스터디 참여현황 조회
    @GetMapping("/study/member/{study_id}")
    public List<ApplicationlistDto> getParticipationlist(@PathVariable Long study_id, HttpServletRequest request){
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

        List<ApplicationlistDto> myApplicationlist = new ArrayList<>();
        for (Applicationlist application : applicationlist){
            // 본인은 제외하고 보여줌
            if (application.getUser_id().equals(user_id)) continue;

            ApplicationlistDto myApplication = new ApplicationlistDto();
            myApplication.setUser_id(application.getUser_id());
            myApplication.setPermission(application.getPermission());
            myApplicationlist.add(myApplication);
        }
        return myApplicationlist;
    }

    // 스터디 참여현황 수정
    @PutMapping("/study/member/{study_id}")
    public ResponseEntity<Response> updateParticipationlist(@PathVariable Long study_id, @RequestBody List<ApplicationlistDto> requestDto, HttpServletRequest request){
        return studyService.updateParticipationlist(study_id, requestDto, request);
    }

    // ------신청한 스터디 관리--------

    // 신청한 스터디 전체 조회
    @GetMapping("/study/applicationlist")
    public List<StudyReturnDto> getApplicationlist(HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        List<Applicationlist> applicationlist = applicationlistRepository.findAllByUser_id(user_id).orElseThrow(StudyNotFoundException::new);

        List<Study> myApplicationlist = new ArrayList<>();
        for (Applicationlist application : applicationlist){
            Study study = studyRepository.findById(application.getStudy_id()).orElseThrow(StudyNotFoundException::new);
            // 본인이 생성한 스터디는 제외하고 보여줌
            if (study.getUser_id().equals(user_id)) continue;

            myApplicationlist.add(study);
        }
        return studyServicelimpet.getformal(myApplicationlist, user_id);
    }

    // 신청한 스터디 탈퇴
    @DeleteMapping("/study/applicationlist/{study_id}")
    public ResponseEntity<Response> deleteApplicationlist(@PathVariable Long study_id, HttpServletRequest request){
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
                studyService.updateNowman(study_id, -1);

                return new ResponseEntity<> (new Response("success", "delete success"), HttpStatus.OK);
            }
        }
        throw new StudyNotFoundException();
    }
}


