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
    private final Profile_imageRepository profile_imageRepository
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
            User_info tmpui = profile_imageRepository.findByUserIdQuery(tmp.get(i).getUserId())
                    .orElseThrow(UserNotFoundException::new);
            tmpres.setUser_info(tmpui);
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


    //study 신고
    @PostMapping("/study/report/{studyId}")
    public Study reportStudy(@PathVariable Long studyId, @RequestBody StudyReportDto studyReportDto, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);

        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

        return studyService.reportStudy(studyId, userId, studyReportDto);
    }
    //study 신고 취소
    @DeleteMapping("/study/report/{studyId}")
    public Study reportundo(@PathVariable Long studyId, HttpServletRequest request){
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)){
            throw new UserValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        // 에러메세지 수정해야됨
        Long userId = CookieHandler.getUserIdFromCookies(request);

        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);

        return studyService.reportundo(studyId, userId);
    }
    // 신청한 스터디 관리
//    @GetMapping("/study/applicationlist")
//    public List<Applicationlist> getApplicationlist(HttpServletRequest request){
//
//    }

}
