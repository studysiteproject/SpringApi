package com.hong.springapi.service;

import com.hong.springapi.dto.*;
import com.hong.springapi.exception.exceptions.UserNotFoundException;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.StudyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StudyService {
    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategorylistRepository categorylistRepository;
    private final UserRepository userRepository;
    private final User_favoriteRepository user_favoriteRepository;
    private final Study_reportRepository study_reportRepository;
    private final Profile_imageRepository profile_imageRepository;

    @Transactional
    public Study join(StudyRequestDto requestDto){

         Study res =studyRepository.save(Study.builder()
                .title(requestDto.getTitle())
                .userId(requestDto.getUser_id())
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
    public Study addFavorite(Long studyId, Long userId){
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        //user, study validity check

        user_favoriteRepository.save(User_favorite.builder()
                .study_id(study)
                .user_id(user)
                .build()
        );

        return study;
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

    public Study deleteFavorite(Long studyId, Long userId){
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        //user, study validity check

        User_favoriteKey user_favoriteKey = new User_favoriteKey();
        user_favoriteKey.setUser_id(userId);
        user_favoriteKey.setStudy_id(studyId);
        user_favoriteRepository.deleteById(user_favoriteKey);

        return study;
    }


    private final ApplicationlistRepository applicationlistRepository;

    @Transactional
    public Long update(Long studyId, StudyRequestDto requestDto) {
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        study.update(requestDto);
        return studyId;
    }

        // 제목이 공백이면 안됨
        if (requestDto.getTitle().trim().equals("") || requestDto.getMaxman() > 50) throw new BadRequestException();
        // 장소가 공백이면 안됨
        if (requestDto.getPlace().trim().equals("")) throw new BadRequestException();
        // maxman 2이상 이어야함
        if (requestDto.getMaxman() < 2 || requestDto.getMaxman() > 100) throw new BadRequestException();
        // 설명이 공백이면 안됨
        if (requestDto.getDescription().trim().equals("")) throw new BadRequestException();

        study.update(requestDto);
        // 카테고리 수정
        if(requestDto.getTech() != null) {
            addCategory(study, requestDto.getTech());
        }
        return new ResponseEntity<>(new Response("success", "update success"), HttpStatus.OK);
    }

    public ResponseEntity<Response> deleteStudy(Long study_id, HttpServletRequest request) {
        // 유효한 토큰인지 검사
        if (!CookieHandler.checkValidation(request)) {
            throw new TokenValidationException();
        }
        // 본인이 작성한 스터디글인지 검사
        Long user_id = CookieHandler.getUser_idFromCookies(request);
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        if (!study.getUser_id().equals(user_id)) {
            throw new UserValidationException();
        }
        studyRepository.deleteById(study_id);

        return new ResponseEntity<>(new Response("success", "study delete success"), HttpStatus.OK);
    }
    public Study createStudy(StudyRequestDto requestDto) {
        return studyRepository.save(Study.builder()
                // 장소가 공백이면 안됨
                .title(requestDto.getTitle())
                // 유저아이디는 수정 불가능
                .userId(requestDto.getUser_id())
                // maxman 2이상 이어야함
                .maxman(requestDto.getMaxman())
                // 설명이 공백이면 안됨
                .description(requestDto.getDescription())
                // 장소가 공백이면 안됨
                .place(requestDto.getPlace())
                .build()
        );
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
    public Study reportStudy(Long study_id, Long user_id, StudyReportDto studyReportDto) {
        //중복 확인 + 유효성 검증
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
        User_favoriteKey user_favoriteKey = new User_favoriteKey(
                user_id, study_id
        );

        if(study_reportRepository.findById(user_favoriteKey).isPresent()){
            throw new IllegalStateException("이미 신고한 게시글입니다.");
        }

    @Transactional
    public void updateNowman(Long id, int val){
        Study study = studyRepository.findById(id).orElseThrow(StudyNotFoundException::new);
        study.updateNowman(val);
    }
        study_reportRepository.save(Study_report.builder()
                .study_id(study)
                .user_id(user)
                .description(studyReportDto.getDescription())
                .build()
        );
        study.setWarn_cnt(study.getWarn_cnt() + 1);

        return studyRepository.save(study);

    }


    @Transactional
    public Study reportundo(Long study_id, Long user_id){
        //해당 신고내역이 있는 지 확인해야함
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
        User_favoriteKey user_favoriteKey = new User_favoriteKey(
                user_id, study_id
        );

        if(!study_reportRepository.findById(user_favoriteKey).isPresent()){
            throw new IllegalStateException("신고하지 않은 게시물입니다.");
        }

        study_reportRepository.deleteById(user_favoriteKey);
        study.setWarn_cnt(study.getWarn_cnt() >0 ? study.getWarn_cnt() -1 : 0 );
        return studyRepository.save(study);
    }

}
