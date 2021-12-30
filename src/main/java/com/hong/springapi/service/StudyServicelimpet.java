package com.hong.springapi.service;

import com.hong.springapi.dto.*;
import com.hong.springapi.exception.exceptions.BadRequestException;
import com.hong.springapi.exception.exceptions.UserNotFoundException;
import com.hong.springapi.exception.exceptions.UserValidationException;
import com.hong.springapi.model.*;
import com.hong.springapi.repository.*;
import com.hong.springapi.response.Response;
import com.hong.springapi.util.CookieHandler;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StudyServicelimpet {
    private final StudyRepository studyRepository;
    private final TechnologylistRepository technologylistRepository;
    private final CategorylistRepository categorylistRepository;
    private final UserRepository userRepository;
    private final User_favoriteRepository user_favoriteRepository;
    private final Study_reportRepository study_reportRepository;
    private final Profile_imageRepository profile_imageRepository;
    private final ApplicationlistRepository applicationlistRepository;

    @Transactional
    public ResponseEntity<Response> join(StudyRequestDto requestDto, Long user_id){
        //tech가 DB에 없는 경우 예외처리 필요
         Study res =studyRepository.save(Study.builder()
                .title(requestDto.getTitle())
                .user_id(user_id)
                .place(requestDto.getPlace())
                .maxman(requestDto.getMaxman())
                .warn_cnt(0)
                .nowman(1)
                .description(requestDto.getDescription())
                .build()
        );
         if(requestDto.getTech()!=null) {
             addCategory(studyRepository.findById(res.getId()).orElseThrow(StudyNotFoundException :: new), requestDto.getTech());
         }

        return new ResponseEntity<Response>(new Response("success", "create success"), HttpStatus.OK);

    }

    @Transactional
    public void addCategory(Study study, List<String> tech){

        for(int i=0; i<tech.size(); i++){
            categorylistRepository.save(Categorylist.builder()
                    .study_id(study)
                    .tech_id(technologylistRepository.findByName(tech.get(i))
                            .orElseThrow(BadRequestException::new))
                    .build()
            );
        }
    }

    @Transactional
    public ResponseEntity<Response> addFavorite(Long studyId, Long user_id){
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
        //user, study validity check

        //중복 검사
        if(user_favoriteRepository.findByUser_favoriteKey(user.getId(), study.getId()).isPresent()){
            throw new BadRequestException("이미 즐겨찾기한 게시글입니다.");
        }

        user_favoriteRepository.save(User_favorite.builder()
                .study_id(study)
                .user_id(user)
                .build()
        );

        return new ResponseEntity<Response>(new Response("success", "add favorite success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> deleteFavorite(Long studyId, Long user_id){
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
        //user, study validity check

        if(!user_favoriteRepository.findByUser_favoriteKey(user_id, studyId).isPresent()){
            //즐겨찾기 안 되어 있는 경우
            throw new BadRequestException();
        }
        User_favoriteKey user_favoriteKey = new User_favoriteKey();
        user_favoriteKey.setUser_id(user_id);
        user_favoriteKey.setStudy_id(studyId);
        user_favoriteRepository.deleteById(user_favoriteKey);

        return new ResponseEntity<Response>(new Response("success", "delete success"), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> reportStudy(Long study_id, Long user_id, StudyReportDto studyReportDto) {
        //중복 확인 + 유효성 검증
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
        User_favoriteKey user_favoriteKey = new User_favoriteKey(
                user_id, study_id
        );

        if(study_reportRepository.findById(user_favoriteKey).isPresent()){
            throw new BadRequestException("이미 신고한 게시글입니다.");
        }

        study_reportRepository.save(Study_report.builder()
                .study_id(study)
                .user_id(user)
                .description(studyReportDto.getDescription())
                .build()
        );
        study.setWarn_cnt(study.getWarn_cnt() + 1);

        studyRepository.save(study);

        return new ResponseEntity<Response>(new Response("success", "report success"), HttpStatus.OK);
    }


    @Transactional
    public ResponseEntity<Response> reportundo(Long study_id, Long user_id){
        //해당 신고내역이 있는 지 확인해야함
        Study study = studyRepository.findById(study_id).orElseThrow(StudyNotFoundException::new);
        User user = userRepository.findById(user_id).orElseThrow(UserNotFoundException::new);
        User_favoriteKey user_favoriteKey = new User_favoriteKey(
                user_id, study_id
        );

        if(!study_reportRepository.findById(user_favoriteKey).isPresent()){
            throw new BadRequestException("신고하지 않은 게시물입니다.");
        }

        study_reportRepository.deleteById(user_favoriteKey);
        study.setWarn_cnt(study.getWarn_cnt() >0 ? study.getWarn_cnt() -1 : 0 );
        studyRepository.save(study);

        return new ResponseEntity<Response>(new Response("success", "report undo success"), HttpStatus.OK);
    }

    public List<StudyReturnDto> getformal(List<Study> tmp, Long clientId){
        //tmp는 스터디 목록, clientId는 쿠키로부터 추출한 user_id(사용 x면 0)
        List<StudyReturnDto> res = new ArrayList<StudyReturnDto>();
        for(int i=0; i<tmp.size(); i++){
            Long studyId;
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

            if (clientId != 0L){
                if(user_favoriteRepository.findByUser_favoriteKey(
                        clientId, studyId).isPresent()){
                    tmpres.setIsfavorite(true);
                }
            }
            //user_info 불러오기 + 작성자 유효성 검증

            Optional<User_info> tmpui =
                    profile_imageRepository.findByUser_idQuery(tmp.get(i).getUser_id());
            //작성자가 존재하지 않으면 스킵
            if(!tmpui.isPresent())continue;

            tmpres.setUser_info(tmpui.get());
            //push
            res.add(tmpres);
        }

        return res;
    }
}
