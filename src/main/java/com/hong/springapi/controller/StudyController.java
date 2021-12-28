package com.hong.springapi.controller;

import com.hong.springapi.dto.*;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.exception.exceptions.UserNotFoundException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import com.hong.springapi.service.StudyService;
import com.hong.springapi.service.StudyService;
import com.hong.springapi.util.CookieHandler;
import jdk.internal.vm.compiler.word.UnsignedWord;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
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
    private final UserRepository userRepository;
    private final Profile_imageRepository profile_imageRepository;
    // create
    @PostMapping("/study")
    public Study createStudy(@RequestBody StudyRequestDto requestDto){
        return studyService.join(requestDto);
    }

    // read all
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

        for(int i=0; i<tmp.size(); i++){
            Long clientId, studyId;
            StudyReturnDto tmpres = new StudyReturnDto();
            //study 복제
            studyId = tmp.get(i).getId();
            tmpres.setId(studyId);
            tmpres.setTitle(tmp.get(i).getTitle());
            tmpres.setMaxman(tmp.get(i).getMaxman());
            tmpres.setNowman(tmp.get(i).getNowman());
            tmpres.setWarn_cnt(tmp.get(i).getWarn_cnt());
            tmpres.setPlace(tmp.get(i).getPlace());
            tmpres.setCreate_date(tmp.get(i).getCreate_date());
            tmpres.setCategory(tmp.get(i).getCategory());
            //tech 불러오기
            tmpres.setTech_info(categorylistRepository.findAllByStudy_idQuery(studyId));
            //favorite 불러오기
            Map<String,String> cookiemap = CookieHandler.getCookies(request);
            if (!cookiemap.isEmpty()){
                if (!CookieHandler.checkValidation(request)){
                    throw new UserValidationException();
                }
                clientId = Long.valueOf(cookiemap.get("index"));
                if(user_favoriteRepository.findByUser_favoriteKey(
                        clientId, studyId).isPresent()){
                    tmpres.setIsfavorite(true);
                }
            }
            //user_info 불러오기 + 작성자 유효성 검증

            Optional<User_info> tmpui =
                    profile_imageRepository.findByUserIdQuery(tmp.get(i).getUserId());
            //작성자가 존재하지 않으면 스킵
            if(!tmpui.isPresent())continue;

            tmpres.setUser_info(tmpui.get());
            //push
            res.add(tmpres);
        }

        return res;
    }

    //add favoritelist
    @PostMapping("/study/favorite/{study_id}")
    public Study addfavorite(@PathVariable Long studyId, HttpServletRequest request){
        //추후 쿠키인증해서 user id 받아오기
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);


        return studyService.addFavorite(studyId, userId);
    }
    @DeleteMapping("/study/favorite/{study_id}")
    public Study deletefavorite(@PathVariable Long studyId, HttpServletRequest request){
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

        return studyService.deleteFavorite(studyId, userId);

    }
    @GetMapping("/study/favorite")
    public List<Study> getFavorites(HttpServletRequest request){
        //추후 쿠키 인증해서 직접 받아오기
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);

        return user_favoriteRepository.findDistinctAllByUser_idQuery(userId);
    }


    // read one
    @GetMapping("/study/{studyId}")
    public Study getStudy(@PathVariable Long studyId){
        return studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
    }

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
    public List<Study> getCreatedStudy(HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new TokenValidationException();
        }
        Long user_id = CookieHandler.getUser_idFromCookies(request);

        return studyRepository.findAllByUser_id(user_id).orElseThrow(UserValidationException::new);
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
    public List<Study> getApplicationlist(HttpServletRequest request){
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
       return myApplicationlist;
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


