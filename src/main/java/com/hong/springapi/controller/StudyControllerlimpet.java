package com.hong.springapi.controller;

import com.hong.springapi.dto.*;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.TokenValidationException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import com.hong.springapi.response.Response;
import com.hong.springapi.service.StudyServicelimpet;
import com.hong.springapi.util.CookieHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class StudyControllerlimpet {

    private final StudyRepository studyRepository;
    private final StudyServicelimpet studyServicelimpet;
    private final ApplicationlistRepository applicationlistRepository;
    private final CategorylistRepository categorylistRepository;
    private final User_favoriteRepository user_favoriteRepository;
    private final UserRepository userRepository;
    private final Profile_imageRepository profile_imageRepository;

    // create study
    @PostMapping("/study")
    public ResponseEntity<Response> createStudy(@RequestBody StudyRequestDto requestDto, HttpServletRequest request){
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);

        return studyServicelimpet.join(requestDto, user_id);
    }

    // read all (main page)
    @GetMapping("/study")
    public List<StudyReturnDto> getStudys(@ModelAttribute SearchRequestDto searchRequestDto, HttpServletRequest request){
        List<StudyReturnDto> res = new ArrayList<StudyReturnDto>();
        List<Study> tmp = new ArrayList<Study>();

        if(searchRequestDto.getTech() == null && searchRequestDto.getCategory() == null){
            tmp.addAll(studyRepository.findAllByTitleAndPlaceQuery(
                    searchRequestDto.getTitle(),
                    searchRequestDto.getPlace()
            ));
        }
        else if(searchRequestDto.getTech() == null) {
            //검색 조건에 tech가 없을 시
            tmp.addAll(categorylistRepository.findDistinctAllByTitleAndPlaceAndCategoryQuery(
                    searchRequestDto.getTitle(),
                    searchRequestDto.getPlace(),
                    searchRequestDto.getCategory()));
        }
        else {
            //검색 조건에 tech가 있을 시
            tmp.addAll(categorylistRepository.findDistinctAllByTitleAndPlaceAndTechQuery
                    (searchRequestDto.getTitle(),
                            searchRequestDto.getPlace(),
                            searchRequestDto.getCategory(),
                            searchRequestDto.getTech()));
        }
        //get clientId
        Long clientId = 0L;

        Map<String,String> cookiemap = CookieHandler.getCookies(request);
        if (!cookiemap.isEmpty()){
            if (!CookieHandler.checkValidation(request)){
                throw new UserValidationException();
            }
            clientId = Long.valueOf(cookiemap.get("index"));
        }

        res = studyServicelimpet.getformal(tmp,clientId);

        return res;
    }

    //add favoritelist
    @PostMapping("/study/favorite/{study_id}")
    public ResponseEntity<Response> addfavorite(@PathVariable Long study_id, HttpServletRequest request){
        //추후 쿠키인증해서 user id 받아오기
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);


        return studyServicelimpet.addFavorite(study_id, user_id);
    }
    @DeleteMapping("/study/favorite/{study_id}")
    public ResponseEntity<Response> deletefavorite(@PathVariable Long study_id, HttpServletRequest request){
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);

        return studyServicelimpet.deleteFavorite(study_id, user_id);

    }
    @GetMapping("/study/favorite")
    public List<StudyReturnDto> getFavorites(HttpServletRequest request){
        //추후 쿠키 인증해서 직접 받아오기
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        List<Study> tmp =  new ArrayList<>();

        tmp.addAll(user_favoriteRepository.findDistinctAllByUser_idQuery(user_id));

        return studyServicelimpet.getformal(tmp,user_id);

    }


    // read one
    @GetMapping("/study/{study_id}")
    public Study getStudy(@PathVariable Long study_id){
        return studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
    }

    //study 신고
    @PostMapping("/study/report/{study_id}")
    public ResponseEntity<Response> reportStudy(@PathVariable Long study_id, @RequestBody StudyReportDto studyReportDto, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);

        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);

        return studyServicelimpet.reportStudy(study_id, user_id, studyReportDto);

    }
    //study 신고 취소
    @DeleteMapping("/study/report/{study_id}")
    public ResponseEntity<Response> reportundo(@PathVariable Long study_id, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);

        //Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);

        return studyServicelimpet.reportundo(study_id, user_id);
    }
    // 신청한 스터디 관리
//    @GetMapping("/study/applicationlist")
//    public List<Applicationlist> getApplicationlist(HttpServletRequest request){
//
//    }

}
