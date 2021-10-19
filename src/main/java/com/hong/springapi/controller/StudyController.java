package com.hong.springapi.controller;

import com.hong.springapi.dto.GetFavoriteDto;
import com.hong.springapi.dto.SearchRequestDto;
import com.hong.springapi.dto.ApplicationlistDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.TokenValidationException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.ApplicationlistKey;
import com.hong.springapi.model.Study;
import com.hong.springapi.model.UserResume;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.CategorylistRepository;
import com.hong.springapi.repository.StudyRepository;
import com.hong.springapi.repository.User_favoriteRepository;
import com.hong.springapi.response.Response;
import com.hong.springapi.service.StudyService;
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
        return user_favoriteRepository.findDistinctAllByUser_idQuery(user_id);
    }

    // read one
    @GetMapping("/study/{studyId}")
    public Study getStudy(@PathVariable Long studyId){
        return studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
    }

    // update
    @PutMapping("/study/{studyId}")
    public ResponseEntity<Response> updateStudy(@PathVariable Long studyId, @RequestBody StudyRequestDto requestDto, HttpServletRequest request){
        return studyService.updateStudy(studyId,requestDto,request);
    }

    // delete
    // 본인 확인 코드 필요
    @DeleteMapping("/study/{studyId}")
    public ResponseEntity<Response> deleteStudy(@PathVariable Long studyId, HttpServletRequest request) throws StudyNotFoundException {
        return studyService.deleteStudy(studyId,request);
    }

    // ------생성한 스터디 관리--------

    // 생성한 스터디 전체 불러오기
    @GetMapping("/study/created")
    public List<Study> getCreatedStudy(HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }

        Long userId = null;
        if (CookieHandler.checkValidation(request)){
            userId = CookieHandler.getUserIdFromCookies(request);
        }
        return studyRepository.findAllByUserId(userId).orElseThrow(UserValidationException::new);
    }

    // 스터디 참여현황 조회
    @GetMapping("/study/member/{studyId}")
    public List<ApplicationlistDto> getParticipationlist(@PathVariable Long studyId, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long userId = CookieHandler.getUserIdFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        if (!study.getUserId().equals(userId)){
            throw new StudyNotFoundException();
        }

        List<Applicationlist> applicationlist = applicationlistRepository.findAllByStudyId(studyId).orElseThrow(StudyNotFoundException::new);

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
    public ResponseEntity<Response> updateParticipationlist(@PathVariable Long studyId, @RequestBody List<ApplicationlistDto> requestDto, HttpServletRequest request){
        return studyService.updateParticipationlist(studyId, requestDto, request);
    }

//    // 스터디 참여자 이력서 조회
//    @GetMapping("/study/member/resume/{user_id}")
//    public UserResume getUserResume(@PathVariable Long userId){
//
//    }

    // ------신청한 스터디 관리--------

    // 신청한 스터디 전체 조회
    @GetMapping("/study/applicationlist")
    public List<String> getApplicationlist(HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        Long userId = CookieHandler.getUserIdFromCookies(request);
       List<Applicationlist> applicationlist = applicationlistRepository.findAllByUserId(userId).orElseThrow(StudyNotFoundException::new);

       List<String> myApplicationlist = new ArrayList<>();
       for (Applicationlist application : applicationlist){
           myApplicationlist.add(String.valueOf(application.getStudyId()));
       }
       return myApplicationlist;
    }

    // 신청한 스터디 탈퇴
    @DeleteMapping("/study/applicationlist/{studyId}")
    public ResponseEntity<Response> deleteApplicationlist(@PathVariable Long studyId, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 신청한 스터디글인지 검사
        Long userId = CookieHandler.getUserIdFromCookies(request);
        List<Applicationlist> myApplicationlist = applicationlistRepository.findAllByUserId(userId).orElseThrow(StudyNotFoundException::new);

        for (Applicationlist myApplication : myApplicationlist){
            if (myApplication.getStudyId().equals(studyId)){
                applicationlistRepository.delete(myApplication);
                return new ResponseEntity<> (new Response("success", "delete success"), HttpStatus.OK);
            }
        }
        return new ResponseEntity<> (new Response("fail", "delete fail"), HttpStatus.BAD_REQUEST);
    }
}


