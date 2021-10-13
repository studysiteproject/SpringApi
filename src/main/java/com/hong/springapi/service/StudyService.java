package com.hong.springapi.service;

import com.hong.springapi.dto.ApplicationlistDto;
import com.hong.springapi.dto.StudyRequestDto;
import com.hong.springapi.exception.exceptions.StudyNotFoundException;
import com.hong.springapi.model.Applicationlist;
import com.hong.springapi.model.Study;
import com.hong.springapi.repository.ApplicationlistRepository;
import com.hong.springapi.repository.StudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StudyService {

    private final StudyRepository studyRepository;
    private final ApplicationlistRepository applicationlistRepository;

    @Transactional
    public Long update(Long studyId, StudyRequestDto requestDto) {
        Study study = studyRepository.findById(studyId).orElseThrow(StudyNotFoundException::new);
        study.update(requestDto);
        return studyId;
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
    public ResponseEntity<String> updateParticipationlist(Long studyId, List<ApplicationlistDto> requestDtolist) {
        List<Applicationlist> applicationlist = applicationlistRepository.findAllByStudyId(studyId);

        for (Applicationlist application : applicationlist){
            for (ApplicationlistDto requestDto : requestDtolist){
                if (requestDto.getUser_id().equals(application.getUserId()))
                    application.update(requestDto.getPermission());
            }
        }
        return new ResponseEntity<> ("success", HttpStatus.OK);
    }
}
