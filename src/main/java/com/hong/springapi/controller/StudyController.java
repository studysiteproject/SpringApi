package com.hong.springapi.controller;

import com.hong.springapi.dto.GetFavoriteDto;
import com.hong.springapi.dto.SearchRequestDto;
import com.hong.springapi.dto.ApplicationlistDto;
import com.hong.springapi.dto.GetFavoriteDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.CategorylistRepository;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.repository.User_favoriteRepository;
import com.hong.springapi.service.StudyService;
import com.hong.springapi.service.StudyService;
import com.hong.springapi.util.CookieHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hong.springapi.util.CookieHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class StudyController {

    private final StudyRepository studyRepository;
    private final StudyService studyService;
    private final ApplicationlistRepository applicationlistRepository;
    private final CategorylistRepository categorylistRepository;
    private final User_favoriteRepository user_favoriteRepository;
    // create
    @PostMapping("/study")
    public Study createStudy(@RequestBody StudyRequestDto requestDto){
       return studyService.join(requestDto);
    }

    // read all
    @GetMapping("/study")
    public List<Study> getStudys(@ModelAttribute SearchRequestDto searchRequestDto){

        if(searchRequestDto.getTech() == null ) {
            return studyRepository.findAllByTitleAndPlaceQuery(
                    searchRequestDto.getTitle(), searchRequestDto.getPlace());
        }
        else {
            return categorylistRepository.findDistinctAllByTitleAndPlaceAndTechQuery
                    (searchRequestDto.getTitle(), searchRequestDto.getPlace(),
                            searchRequestDto.getTech());
        }
    }

    //add favoritelist
    @PostMapping("/study/favorite")
    public Study addfavorite(@RequestBody GetFavoriteDto getFavoriteDto){
        //추후 쿠키인증해서 user id 받아오기
        return studyService.addFavorite(getFavoriteDto);
    }

    @GetMapping("/study/favorite")
    public List<Study> getFavorites(@RequestParam(value = "id") Long user_id){
        //추후 쿠키 인증해서 직접 받아오기
        return studyService.getFavoritelist(user_id);
    }


    // read one
    @GetMapping("/study/{studyId}")
    public Study getStudy(@PathVariable Long studyId){
        return studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
    }

    // update
    @PutMapping("/study/{studyId}")
    public Long updateStudy(@PathVariable Long studyId, @RequestBody StudyRequestDto requestDto, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (!study.getUserId().equals(userId)){
            throw new StudyNotFoundException();
        }

        return studyService.update(studyId,requestDto);
    }

    // delete
    // 본인 확인 코드 필요
    @DeleteMapping("/study/{studyId}")
    public Long deleteStudy(@PathVariable Long studyId, HttpServletRequest request) throws StudyNotFoundException {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (!study.getUserId().equals(userId)){
            throw new StudyNotFoundException();
        }
        studyRepository.deleteById(studyId);
        return studyId;
    }

    // ------생성한 스터디 관리--------

    // 생성한 스터디 전체 불러오기
    @GetMapping("/study/created")
    public List<Study> getCreatedStudy(HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }

        Long userId = null;
        if (CookieHandler.checkValidation(request)){
            userId = CookieHandler.getUserIdFromCookies(request);
        }
        return studyRepository.findAllByUserId(userId).orElseThrow(UserValidationException::new);
    }

    // 스터디 참여현황 조회
    // 에러처리 뭐로하지?
    @GetMapping("/study/member/{studyId}")
    public List<ApplicationlistDto> getParticipationlist(@PathVariable Long studyId, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }

        List<Applicationlist> applicationlist = applicationlistRepository.findAllByStudyId(studyId);

        List<ApplicationlistDto> myApplicationlist = new ArrayList<>();
        for (Applicationlist application : applicationlist){
            ApplicationlistDto myApplication = new ApplicationlistDto();
            myApplication.setUser_id(application.getUserId());
            myApplication.setPermission(application.getPermission());
            myApplicationlist.add(myApplication);
        }
        return myApplicationlist;
    }

    // 스터디 참여현황 수정
    @PutMapping("/study/member/{studyId}")
    public ResponseEntity<String> updateParticipationlist(@PathVariable Long studyId, @RequestBody List<ApplicationlistDto> requestDto, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (!study.getUserId().equals(userId)){
            throw new StudyNotFoundException();
        }
        return studyService.updateParticipationlist(studyId, requestDto);
    }

    // 신청한 스터디 관리
//    @GetMapping("/study/applicationlist")
//    public List<Applicationlist> getApplicationlist(HttpServletRequest request){
//
//    }

}


