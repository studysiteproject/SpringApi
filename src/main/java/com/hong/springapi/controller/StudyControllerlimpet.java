package com.hong.springapi.controller;

import com.hong.springapi.dto.*;
import com.hong.springapi.exception.exceptions.BadRequestException;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.TokenValidationException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import com.hong.springapi.response.Response;
import com.hong.springapi.service.StudyServicelimpet;
import com.hong.springapi.util.CookieHandler;
import com.hong.springapi.util.SQLdefend;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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
    @PostMapping("/study/create")
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

        System.out.println(searchRequestDto.getTitle());
        if(searchRequestDto.getTech() == null){
            tmp.addAll(studyRepository.findAllByTitleAndPlaceQuery(
                    searchRequestDto.getTitle(),
                    searchRequestDto.getPlace(),
                    searchRequestDto.getCategory()
            ));
        }
        else {
            //검색 조건에 tech가 있을 시
            tmp.addAll(categorylistRepository.findDistinctAllByTitleAndPlaceAndTechQuery
                    (searchRequestDto.getTitle(),
                            searchRequestDto.getPlace(),
                            searchRequestDto.getCategory(),
                            searchRequestDto.getTech()));
        }
//        if(tmp.isEmpty()){
//            throw new BadRequestException("텅~");
//        }

        //get clientId
        Long clientId = 0L;

        Map<String,String> cookiemap = CookieHandler.getCookies(request);
        if (!cookiemap.isEmpty()){
//            if (!CookieHandler.checkValidation(request)){
//                throw new UserValidationException();
//            }
            clientId = Long.valueOf(cookiemap.get("index"));
        }

        res = studyServicelimpet.getformal(tmp,clientId);

        return res;
    }

    //add favoritelist
    @GetMapping("/study/favorite/{study_id}")
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
    //즐겨찾기 취소
    @DeleteMapping("/study/favorite/{study_id}")
    public ResponseEntity<Response> deletefavorite(@PathVariable Long study_id, HttpServletRequest request){
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);

        return studyServicelimpet.deleteFavorite(study_id, user_id);

    }

    //즐겨찾기 목록
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
    public StudyDetailDto getStudy(@PathVariable Long study_id, HttpServletRequest request){
        Long clientId = 0L;

        Map<String,String> cookiemap = CookieHandler.getCookies(request);
        if (!cookiemap.isEmpty()){
            clientId = Long.valueOf(cookiemap.get("index"));
        }

        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        return studyServicelimpet.getformal(study, clientId);
    }

    // read one for update
    @GetMapping("/study/check/{study_id}")
    public Map<String, Boolean> checkStudyWriter(@PathVariable Long study_id, HttpServletRequest request){
        Long clientId = 0L;
        Map<String, Boolean> res = new HashMap<>();
        Map<String,String> cookiemap = CookieHandler.getCookies(request);
        //로그인 확인
        if (!cookiemap.isEmpty()){
            clientId = Long.valueOf(cookiemap.get("index"));
        }
        else{
            throw new TokenValidationException();
        }

        //스터디가 존재하는 지 확인
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);

        //작성자가 맞을 시
        if(study.getUser_id() == clientId){
            res.put("iswriter",true);
        }
        else{
            res.put("iswriter",false);
        }
        return res;
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
    // 스터디 참가 신청
    @PostMapping("/study/recruit/{study_id}")
    public ResponseEntity<Response> recruitStudy(@PathVariable Long study_id, @RequestBody StudyReportDto studyReportDto, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 에러메세지 수정해야됨
        Long user_id = CookieHandler.getUser_idFromCookies(request);

        return studyServicelimpet.recruitStudy(study_id, user_id,studyReportDto);
    }

}
